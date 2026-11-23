package io.github.taichi0373.kumamoto_henno_map.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.taichi0373.kumamoto_henno_map.repository.dao.PasswordResetTokensDao;
import io.github.taichi0373.kumamoto_henno_map.repository.dao.UsersDao;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.PasswordResetTokensEntity;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.SystemField;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.UsersEntity;
import io.github.taichi0373.kumamoto_henno_map.util.TokenUtils;

/**
 * パスワードリセットサービス
 * <p>
 * パスワードリセットトークンの生成・検証・メール送信・パスワード更新処理を提供する。
 * </p>
 */
@Service
public class PasswordResetService {

    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);

    /** トークン有効期限（分） */
    private static final int TOKEN_EXPIRY_MINUTES = 30;

    /** ユーザーDAO */
    @Autowired
    private UsersDao usersDao;

    /** パスワードリセットトークンDAO */
    @Autowired
    private PasswordResetTokensDao passwordResetTokensDao;

    /** パスワードエンコーダー */
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /** メール送信サービス */
    @Autowired
    private MailService mailService;

    /**
     * パスワードリセットメールを送信する
     * <p>
     * 指定メールアドレスに対応するユーザーが存在する場合にリセットトークンを生成してメールを送信する。
     * ユーザーが存在しない場合も列挙攻撃対策のため正常終了する。
     * </p>
     * @param email 送信先メールアドレス
     */
    public void requestPasswordReset(String email) {
        UsersEntity user = usersDao.selectByEmail(email);
        // ユーザーが存在しない場合は処理をスキップ（列挙攻撃対策のためエラーを返さない）
        if (user == null) {
            return;
        }

        String token = TokenUtils.generateToken();

        // トークンをDBに保存（DB漏えい対策のためSHA-256ハッシュのみ保存）
        LocalDateTime now = LocalDateTime.now();
        PasswordResetTokensEntity tokenEntity = new PasswordResetTokensEntity();
        tokenEntity.setUserId(user.getUserId());
        tokenEntity.setToken(TokenUtils.hashToken(token));
        tokenEntity.setExpiresAt(now.plusMinutes(TOKEN_EXPIRY_MINUTES));
        tokenEntity.setUsed(false);
        tokenEntity.setSystemField(new SystemField(now, now));
        passwordResetTokensDao.insert(tokenEntity);

        // パスワードリセットメールを送信
        String resetUrl = mailService.getFrontendBaseUrl() + "/reset-password?token=" + token;
        String body = "パスワードリセットのリクエストを受け付けました。\n\n"
            + "以下のURLからパスワードをリセットしてください。\n"
            + "このURLの有効期限は30分です。\n\n"
            + resetUrl + "\n\n"
            + "このメールに心当たりがない場合は、本メールを破棄してください。\n"
            + "なお、このメールアドレスへの返信はできません。";
        mailService.send(email, "【自主返納特典マップ】パスワードリセットのご案内", body);
    }

    /**
     * パスワードリセットを実行する
     * <p>
     * トークンを検証し、有効な場合に新しいパスワードを設定する。
     * DBにはハッシュのみ保存されているため、照合時に同じSHA-256ハッシュを計算して比較する。
     * used=false かつ期限内の条件付き UPDATE を原子的に実行することで、
     * 同時リクエストによるトークンの二重使用を防ぐ。
     * リセットメールの受信によりメールアドレスの所有が確認できたため、未確認の場合は確認済みにする。
     * </p>
     * @param token リセットトークン（平文）
     * @param newPassword 新しいパスワード（平文）
     * @return 成功時はtrue、トークンが無効・期限切れ・使用済みの場合はfalse
     */
    @Transactional
    public boolean confirmPasswordReset(String token, String newPassword) {
        String tokenHash = TokenUtils.hashToken(token);
        PasswordResetTokensEntity tokenEntity = passwordResetTokensDao.selectByToken(tokenHash);

        LocalDateTime now = LocalDateTime.now();

        // トークンの存在チェック
        if (tokenEntity == null) {
            return false;
        }

        // used=false かつ期限内の場合のみ used=true に更新する原子的操作
        // 0件更新 = 使用済みまたは期限切れ（同時リクエスト時も必ず片方が0件になる）
        int updated = passwordResetTokensDao.markAsUsedIfValid(tokenHash, now);
        if (updated == 0) {
            return false;
        }

        // ユーザーのパスワードを更新
        UsersEntity user = usersDao.selectById(tokenEntity.getUserId());
        if (user == null) {
            return false;
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        if (user.getEmailVerifiedAt() == null) {
            user.setEmailVerifiedAt(now);
        }
        SystemField currentSystemField = user.getSystemField();
        LocalDateTime sysCreatedAt = currentSystemField != null && currentSystemField.getSysCreatedAt() != null
                ? currentSystemField.getSysCreatedAt()
                : now;
        user.setSystemField(new SystemField(sysCreatedAt, now));
        usersDao.update(user);

        return true;
    }

    /**
     * 期限切れ・使用済みトークンの定期クリーンアップ
     * <p>
     * 毎日午前2時に実行し、expires_at が現在時刻以前または used=true のトークンを削除する。
     * ON DELETE CASCADE でユーザー削除時の孤児化は防いでいるが、
     * アクティブユーザーのトークン蓄積はこのジョブで対処する。
     * </p>
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupExpiredTokens() {
        int deleted = passwordResetTokensDao.deleteExpiredOrUsed(LocalDateTime.now());
        logger.info("パスワードリセットトークンのクリーンアップ完了: {}件削除", deleted);
    }

}
