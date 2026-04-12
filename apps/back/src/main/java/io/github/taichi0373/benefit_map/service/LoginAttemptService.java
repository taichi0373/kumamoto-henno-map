package io.github.taichi0373.benefit_map.service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * ログイン試行管理サービス
 * <p>
 * ログイン・新規登録・パスワードリセットのIPアドレスごとの試行回数を管理し、
 * 過剰なアクセスをブロックする。
 * </p>
 * <p>
 * キャッシュには Caffeine を使用し、最大件数（{@link #MAX_TRACKED_IPS}）と
 * TTL（各操作のロックアウト時間）によりメモリ使用量を制限する。
 * TTL は最終書き込み時刻から起算するため、期限内に再試行がなければ自動削除される。
 * </p>
 * <p>
 * <strong>【注意】単一インスタンス前提の実装</strong><br>
 * 試行回数はインメモリ管理のため、複数インスタンスで水平スケールする構成では
 * 制限がインスタンス間で共有されず、ブルートフォース対策が無効化される。<br>
 * 水平スケールが必要になった場合は Redis 等の共有ストアへの移行を検討すること。
 * </p>
 */
@Service
public class LoginAttemptService {

    /** ログイン: 最大失敗試行回数 */
    private static final int LOGIN_MAX_ATTEMPTS = 5;

    /** ログイン: ロックアウト時間（分） */
    private static final long LOGIN_LOCK_MINUTES = 15;

    /** 新規登録: IP当たりの最大試行回数 */
    private static final int SIGNUP_MAX_ATTEMPTS = 5;

    /** 新規登録: ロックアウト時間（分） */
    private static final long SIGNUP_LOCK_MINUTES = 60;

    /** パスワードリセット申請: IP当たりの最大試行回数 */
    private static final int PASSWORD_RESET_REQUEST_MAX_ATTEMPTS = 5;

    /** パスワードリセット申請: ロックアウト時間（分） */
    private static final long PASSWORD_RESET_REQUEST_LOCK_MINUTES = 15;

    /** パスワードリセット確認: 最大失敗試行回数 */
    private static final int PASSWORD_RESET_MAX_ATTEMPTS = 5;

    /** パスワードリセット確認: ロックアウト時間（分） */
    private static final long PASSWORD_RESET_LOCK_MINUTES = 15;

    /** キャッシュに保持するIPアドレスの最大件数（超過時は古いエントリをLRU削除） */
    private static final long MAX_TRACKED_IPS = 10_000L;

    /**
     * 試行情報レコード
     * <p>
     * lockedUntil : ロックアウト解除時刻（上限未達の場合はnull）。<br>
     * エントリ自体のTTLはCaffeineが管理するため、windowExpiresAt は不要。
     * </p>
     */
    private record AttemptData(int count, LocalDateTime lockedUntil) {}

    /** ログイン試行キャッシュ（キー: IPアドレス） */
    private final Cache<String, AttemptData> loginAttempts =
            buildCache(LOGIN_LOCK_MINUTES, MAX_TRACKED_IPS);

    /** 新規登録試行キャッシュ（キー: IPアドレス） */
    private final Cache<String, AttemptData> signupAttempts =
            buildCache(SIGNUP_LOCK_MINUTES, MAX_TRACKED_IPS);

    /** パスワードリセット申請試行キャッシュ（キー: IPアドレス） */
    private final Cache<String, AttemptData> passwordResetRequestAttempts =
            buildCache(PASSWORD_RESET_REQUEST_LOCK_MINUTES, MAX_TRACKED_IPS);

    /** パスワードリセット確認試行キャッシュ（キー: IPアドレス） */
    private final Cache<String, AttemptData> passwordResetAttempts =
            buildCache(PASSWORD_RESET_LOCK_MINUTES, MAX_TRACKED_IPS);

    /**
     * ログイン試行がブロックされているか確認する
     * @param ip クライアントIPアドレス
     * @return ブロック中の場合はtrue
     */
    public boolean isLoginBlocked(String ip) {
        return isBlocked(loginAttempts, ip);
    }

    /**
     * ログイン失敗を記録する
     * @param ip クライアントIPアドレス
     */
    public void recordLoginFailure(String ip) {
        record(loginAttempts, ip, LOGIN_MAX_ATTEMPTS, LOGIN_LOCK_MINUTES);
    }

    /**
     * ログイン成功を記録し、試行カウントをリセットする
     * @param ip クライアントIPアドレス
     */
    public void recordLoginSuccess(String ip) {
        loginAttempts.invalidate(ip);
    }

    /**
     * 新規登録試行がブロックされているか確認する
     * @param ip クライアントIPアドレス
     * @return ブロック中の場合はtrue
     */
    public boolean isSignupBlocked(String ip) {
        return isBlocked(signupAttempts, ip);
    }

    /**
     * 新規登録試行を記録する（成否問わずカウント）
     * @param ip クライアントIPアドレス
     */
    public void recordSignupAttempt(String ip) {
        record(signupAttempts, ip, SIGNUP_MAX_ATTEMPTS, SIGNUP_LOCK_MINUTES);
    }

    /**
     * パスワードリセット申請がブロックされているか確認する
     * @param ip クライアントIPアドレス
     * @return ブロック中の場合はtrue
     */
    public boolean isPasswordResetRequestBlocked(String ip) {
        return isBlocked(passwordResetRequestAttempts, ip);
    }

    /**
     * パスワードリセット申請の試行を記録する（成否問わずカウント）
     * @param ip クライアントIPアドレス
     */
    public void recordPasswordResetRequestAttempt(String ip) {
        record(passwordResetRequestAttempts, ip, PASSWORD_RESET_REQUEST_MAX_ATTEMPTS, PASSWORD_RESET_REQUEST_LOCK_MINUTES);
    }

    /**
     * パスワードリセット確認試行がブロックされているか確認する
     * @param ip クライアントIPアドレス
     * @return ブロック中の場合はtrue
     */
    public boolean isPasswordResetBlocked(String ip) {
        return isBlocked(passwordResetAttempts, ip);
    }

    /**
     * パスワードリセット確認の失敗を記録する（トークン不一致時）
     * @param ip クライアントIPアドレス
     */
    public void recordPasswordResetFailure(String ip) {
        record(passwordResetAttempts, ip, PASSWORD_RESET_MAX_ATTEMPTS, PASSWORD_RESET_LOCK_MINUTES);
    }

    /**
     * パスワードリセット成功時に試行カウントをリセットする
     * @param ip クライアントIPアドレス
     */
    public void recordPasswordResetSuccess(String ip) {
        passwordResetAttempts.invalidate(ip);
    }

    /**
     * ブロック判定
     * <p>
     * ロックアウト期限切れのエントリは無効化してfalseを返す。
     * TTL による自動削除前に呼ばれた場合の保険的チェック。
     * </p>
     */
    private boolean isBlocked(Cache<String, AttemptData> cache, String key) {
        AttemptData data = cache.getIfPresent(key);
        if (data == null || data.lockedUntil() == null) {
            return false;
        }
        if (LocalDateTime.now().isBefore(data.lockedUntil())) {
            return true;
        }
        cache.invalidate(key);
        return false;
    }

    /**
     * 試行を記録し、上限到達時はロックアウト時刻を設定する
     */
    private void record(Cache<String, AttemptData> cache, String key,
            int maxAttempts, long lockMinutes) {
        cache.asMap().compute(key, (k, data) -> {
            int count = (data == null) ? 1 : data.count() + 1;
            LocalDateTime lockedUntil = count >= maxAttempts
                    ? LocalDateTime.now().plusMinutes(lockMinutes)
                    : null;
            return new AttemptData(count, lockedUntil);
        });
    }

    /**
     * 最大サイズとTTLを指定してCaffeineキャッシュを生成する
     * <p>
     * expireAfterWrite: 最終書き込み時刻からTTL経過で自動削除。
     * 未ロックの中途試行エントリも一定時間後に自動削除されるため、
     * メモリ肥大化を防ぐ。
     * maximumSize: 超過時はLRUで古いエントリを削除する。
     * </p>
     * @param expireMinutes TTL（分）
     * @param maxSize 最大保持件数
     * @return Caffeineキャッシュ
     */
    private Cache<String, AttemptData> buildCache(long expireMinutes, long maxSize) {
        return Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireMinutes, TimeUnit.MINUTES)
                .build();
    }
}
