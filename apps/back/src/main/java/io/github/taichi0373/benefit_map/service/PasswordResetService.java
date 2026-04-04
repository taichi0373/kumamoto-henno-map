package io.github.taichi0373.benefit_map.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.taichi0373.benefit_map.repository.dao.PasswordResetTokensDao;
import io.github.taichi0373.benefit_map.repository.dao.UsersDao;
import io.github.taichi0373.benefit_map.repository.entity.PasswordResetTokensEntity;
import io.github.taichi0373.benefit_map.repository.entity.SystemField;
import io.github.taichi0373.benefit_map.repository.entity.UsersEntity;

/**
 * パスワードリセットサービス
 * <p>
 * パスワードリセットトークンの生成・検証・メール送信・パスワード更新処理を提供する。
 * </p>
 */
@Service
public class PasswordResetService {

    /** トークン有効期限（分） */
    private static final int TOKEN_EXPIRY_MINUTES = 30;

    /** トークンのバイト長（256bit = 32バイト、hex文字列で64文字） */
    private static final int TOKEN_BYTE_LENGTH = 32;

    /** ユーザーDAO */
    @Autowired
    private UsersDao usersDao;

    /** パスワードリセットトークンDAO */
    @Autowired
    private PasswordResetTokensDao passwordResetTokensDao;

    /** パスワードエンコーダー */
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /** メール送信クライアント */
    @Autowired
    private JavaMailSender mailSender;

    /** フロントエンドのベースURL */
    @Value("${app.password-reset.base-url:http://localhost:3000}")
    private String frontendBaseUrl;

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

        // SecureRandomで64文字のhexトークンを生成
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[TOKEN_BYTE_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : tokenBytes) {
            sb.append(String.format("%02x", b));
        }
        String token = sb.toString();

        // トークンをDBに保存（DB漏えい対策のためSHA-256ハッシュのみ保存）
        LocalDateTime now = LocalDateTime.now();
        PasswordResetTokensEntity tokenEntity = new PasswordResetTokensEntity();
        tokenEntity.setUserId(user.getUserId());
        tokenEntity.setToken(hashToken(token));
        tokenEntity.setExpiresAt(now.plusMinutes(TOKEN_EXPIRY_MINUTES));
        tokenEntity.setUsed(false);
        tokenEntity.setSystemField(new SystemField(now, now));
        passwordResetTokensDao.insert(tokenEntity);

        // パスワードリセットメールを送信
        String resetUrl = frontendBaseUrl + "/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("【自主返納特典マップ】パスワードリセットのご案内");
        message.setText(
            "パスワードリセットのリクエストを受け付けました。\n\n"
            + "以下のURLからパスワードをリセットしてください。\n"
            + "このURLの有効期限は30分です。\n\n"
            + resetUrl + "\n\n"
            + "このメールに心当たりがない場合は、本メールを破棄してください。\n"
            + "なお、このメールアドレスへの返信はできません。"
        );
        mailSender.send(message);
    }

    /**
     * パスワードリセットを実行する
     * <p>
     * トークンを検証し、有効な場合に新しいパスワードを設定する。
     * DBにはハッシュのみ保存されているため、照合時に同じSHA-256ハッシュを計算して比較する。
     * used=false かつ期限内の条件付き UPDATE を原子的に実行することで、
     * 同時リクエストによるトークンの二重使用を防ぐ。
     * </p>
     * @param token リセットトークン（平文）
     * @param newPassword 新しいパスワード（平文）
     * @return 成功時はtrue、トークンが無効・期限切れ・使用済みの場合はfalse
     */
    @Transactional
    public boolean confirmPasswordReset(String token, String newPassword) {
        String tokenHash = hashToken(token);
        PasswordResetTokensEntity tokenEntity = passwordResetTokensDao.selectByToken(tokenHash);

        // トークンの存在チェック
        if (tokenEntity == null) {
            return false;
        }

        // used=false かつ期限内の場合のみ used=true に更新する原子的操作
        // 0件更新 = 使用済みまたは期限切れ（同時リクエスト時も必ず片方が0件になる）
        int updated = passwordResetTokensDao.markAsUsedIfValid(tokenHash, LocalDateTime.now());
        if (updated == 0) {
            return false;
        }

        // ユーザーのパスワードを更新
        UsersEntity user = usersDao.selectById(tokenEntity.getUserId());
        if (user == null) {
            return false;
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        usersDao.update(user);

        return true;
    }

    /**
     * トークンをSHA-256でハッシュ化する
     * <p>
     * DBには平文トークンを保存せず、このハッシュ値のみを保存する。
     * メールには平文トークンを送信し、照合時に同じハッシュを計算して比較する。
     * </p>
     * @param token 平文トークン
     * @return SHA-256ハッシュ（64文字hex文字列）
     */
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256はJava標準で必ず利用可能なため到達しない
            throw new IllegalStateException("SHA-256アルゴリズムが利用できません", e);
        }
    }

}
