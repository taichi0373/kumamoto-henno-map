package io.github.taichi0373.kumamoto_henno_map.service;

import java.time.LocalDateTime;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.taichi0373.kumamoto_henno_map.repository.dao.EmailVerificationTokensDao;
import io.github.taichi0373.kumamoto_henno_map.repository.dao.UsersDao;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.EmailVerificationTokensEntity;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.SystemField;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.UsersEntity;
import io.github.taichi0373.kumamoto_henno_map.util.TokenUtils;

/**
 * メールアドレス確認サービス
 * <p>
 * 新規登録時・メールアドレス変更時の確認メール送信、確認トークンの検証、
 * 未確認ユーザーと不要トークンの定期削除を提供する。
 * </p>
 */
@Service
public class EmailVerificationService {

    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(EmailVerificationService.class);

    /** トークン有効期限（時間）。未確認ユーザーの自動削除までの時間も同じ値とする */
    static final int TOKEN_EXPIRY_HOURS = 24;

    /** 同一ユーザーへの確認メールの最小送信間隔（秒） */
    static final int RESEND_INTERVAL_SECONDS = 60;

    /** 件名: 新規登録時 */
    private static final String SUBJECT_SIGNUP = "【自主返納特典マップ】メールアドレス確認のお願い";

    /** 件名: メールアドレス変更時 */
    private static final String SUBJECT_EMAIL_CHANGE = "【自主返納特典マップ】メールアドレス変更の確認";

    /**
     * 確認結果
     */
    public enum ConfirmResult {
        /** 確認完了 */
        VERIFIED,
        /** トークンが無効・期限切れ・使用済み */
        INVALID,
        /** 変更後のメールアドレスが他のユーザーに使用されている */
        CONFLICT
    }

    /** ユーザーDAO */
    @Autowired
    private UsersDao usersDao;

    /** メールアドレス確認トークンDAO */
    @Autowired
    private EmailVerificationTokensDao emailVerificationTokensDao;

    /** メール送信サービス */
    @Autowired
    private MailService mailService;

    /**
     * 新規登録時の確認メールを送信する
     * @param user 登録したユーザー（未確認状態）
     */
    public void sendSignupVerification(UsersEntity user) {
        String token = issueToken(user.getUserId(), null);
        String body = "自主返納特典マップへのご登録ありがとうございます。\n\n"
            + "以下のURLを開き、画面のボタンを押してメールアドレスの確認を完了してください。\n"
            + "確認が完了するまでログインできません。\n"
            + "このURLの有効期限は" + TOKEN_EXPIRY_HOURS + "時間です。期限内に確認されない場合、登録は削除されます。\n\n"
            + buildVerifyUrl(token) + "\n\n"
            + "このメールに心当たりがない場合は、本メールを破棄してください。\n"
            + "なお、このメールアドレスへの返信はできません。";
        mailService.send(user.getEmail(), SUBJECT_SIGNUP, body);
    }

    /**
     * メールアドレス変更時の確認メールを変更後のアドレスへ送信する
     * <p>
     * 確認が完了するまでメールアドレスは変更前のまま保持する。
     * 同じ変更後アドレスへの送信は {@link #RESEND_INTERVAL_SECONDS} 秒以内の場合スキップする。
     * </p>
     * @param userId ユーザーID
     * @param newEmail 変更後メールアドレス
     */
    public void sendEmailChangeVerification(Long userId, String newEmail) {
        EmailVerificationTokensEntity latest = emailVerificationTokensDao.selectLatestByUserId(userId);
        if (latest != null && Objects.equals(latest.getNewEmail(), newEmail) && isWithinResendInterval(latest)) {
            logger.info("送信間隔内のためメールアドレス変更の確認メールをスキップしました: userId={}", userId);
            return;
        }
        String token = issueToken(userId, newEmail);
        String body = "メールアドレス変更のリクエストを受け付けました。\n\n"
            + "以下のURLを開き、画面のボタンを押して変更を完了してください。\n"
            + "確認が完了するまでは、変更前のメールアドレスが使用されます。\n"
            + "このURLの有効期限は" + TOKEN_EXPIRY_HOURS + "時間です。\n\n"
            + buildVerifyUrl(token) + "\n\n"
            + "このメールに心当たりがない場合は、本メールを破棄してください。\n"
            + "なお、このメールアドレスへの返信はできません。";
        mailService.send(newEmail, SUBJECT_EMAIL_CHANGE, body);
    }

    /**
     * 新規登録時の確認メールを再送する
     * <p>
     * 指定メールアドレスの未確認ユーザーが存在する場合のみ送信する。
     * 存在しない場合・確認済みの場合・送信間隔内の場合も、列挙攻撃対策のため正常終了する。
     * </p>
     * @param email メールアドレス
     */
    public void resendSignupVerification(String email) {
        UsersEntity user = usersDao.selectByEmail(email);
        if (user == null || user.getEmailVerifiedAt() != null) {
            return;
        }
        EmailVerificationTokensEntity latest = emailVerificationTokensDao.selectLatestByUserId(user.getUserId());
        if (latest != null && isWithinResendInterval(latest)) {
            logger.info("送信間隔内のため確認メールの再送をスキップしました: userId={}", user.getUserId());
            return;
        }
        sendSignupVerification(user);
    }

    /**
     * メールアドレスの確認を確定する
     * <p>
     * used=false かつ期限内の条件付き UPDATE によりトークンを原子的に使用済みにする。
     * 新規登録時のトークンはユーザーを確認済みにし、メールアドレス変更時のトークンは
     * メールアドレスを変更後のアドレスに切り替える。
     * </p>
     * @param token 確認トークン（平文）
     * @return 確認結果
     */
    @Transactional
    public ConfirmResult confirm(String token) {
        String tokenHash = TokenUtils.hashToken(token);
        EmailVerificationTokensEntity tokenEntity = emailVerificationTokensDao.selectByToken(tokenHash);
        if (tokenEntity == null) {
            return ConfirmResult.INVALID;
        }

        LocalDateTime now = LocalDateTime.now();
        if (emailVerificationTokensDao.markAsUsedIfValid(tokenHash, now) == 0) {
            return ConfirmResult.INVALID;
        }

        UsersEntity user = usersDao.selectById(tokenEntity.getUserId());
        if (user == null) {
            return ConfirmResult.INVALID;
        }

        String newEmail = tokenEntity.getNewEmail();
        if (newEmail != null) {
            if (usersDao.existsByEmailExcluding(newEmail, user.getUserId())) {
                return ConfirmResult.CONFLICT;
            }
            user.setEmail(newEmail);
            user.setEmailVerifiedAt(now);
        } else if (user.getEmailVerifiedAt() == null) {
            user.setEmailVerifiedAt(now);
        }
        SystemField currentSystemField = user.getSystemField();
        LocalDateTime sysCreatedAt = currentSystemField != null && currentSystemField.getSysCreatedAt() != null
                ? currentSystemField.getSysCreatedAt()
                : now;
        user.setSystemField(new SystemField(sysCreatedAt, now));
        usersDao.update(user);
        return ConfirmResult.VERIFIED;
    }

    /**
     * 未確認ユーザーと不要トークンの定期クリーンアップ
     * <p>
     * 毎時0分に実行し、作成から {@link #TOKEN_EXPIRY_HOURS} 時間を経過した未確認ユーザーと、
     * 期限切れ・使用済みのトークンを削除する。
     * </p>
     */
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupUnverified() {
        LocalDateTime now = LocalDateTime.now();
        int deletedUsers = usersDao.deleteUnverifiedCreatedBefore(now.minusHours(TOKEN_EXPIRY_HOURS));
        int deletedTokens = emailVerificationTokensDao.deleteExpiredOrUsed(now);
        logger.info("メールアドレス未確認ユーザーのクリーンアップ完了: ユーザー{}件・トークン{}件削除", deletedUsers, deletedTokens);
    }

    /**
     * 確認トークンを発行してDBに保存する
     * <p>
     * 最新のリンクのみを有効にするため、同一ユーザーの未使用トークンは使用済みにする。
     * </p>
     * @param userId ユーザーID
     * @param newEmail 変更後メールアドレス（新規登録時はnull）
     * @return 平文トークン
     */
    private String issueToken(Long userId, String newEmail) {
        LocalDateTime now = LocalDateTime.now();
        emailVerificationTokensDao.markAllAsUsedByUserId(userId, now);

        String token = TokenUtils.generateToken();
        EmailVerificationTokensEntity tokenEntity = new EmailVerificationTokensEntity();
        tokenEntity.setUserId(userId);
        tokenEntity.setToken(TokenUtils.hashToken(token));
        tokenEntity.setNewEmail(newEmail);
        tokenEntity.setExpiresAt(now.plusHours(TOKEN_EXPIRY_HOURS));
        tokenEntity.setUsed(false);
        tokenEntity.setSystemField(new SystemField(now, now));
        emailVerificationTokensDao.insert(tokenEntity);
        return token;
    }

    /**
     * トークンの作成から送信間隔が経過していないか判定する
     * @param tokenEntity トークンエンティティ
     * @return 送信間隔内の場合はtrue
     */
    private boolean isWithinResendInterval(EmailVerificationTokensEntity tokenEntity) {
        SystemField systemField = tokenEntity.getSystemField();
        if (systemField == null || systemField.getSysCreatedAt() == null) {
            return false;
        }
        return systemField.getSysCreatedAt().plusSeconds(RESEND_INTERVAL_SECONDS).isAfter(LocalDateTime.now());
    }

    /**
     * 確認画面のURLを生成する
     * @param token 平文トークン
     * @return 確認画面のURL
     */
    private String buildVerifyUrl(String token) {
        return mailService.getFrontendBaseUrl() + "/verify-email?token=" + token;
    }
}
