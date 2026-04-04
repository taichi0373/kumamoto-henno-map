package io.github.taichi0373.benefit_map.service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * ログイン試行管理サービス
 * <p>
 * ログイン・新規登録のIPアドレスごとの試行回数を管理し、
 * 過剰なアクセスをブロックする。
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

    /** パスワードリセット: 最大失敗試行回数 */
    private static final int PASSWORD_RESET_MAX_ATTEMPTS = 5;

    /** パスワードリセット: ロックアウト時間（分） */
    private static final long PASSWORD_RESET_LOCK_MINUTES = 15;

    /** 試行情報レコード */
    private record AttemptData(int count, LocalDateTime lockedUntil) {}

    /** ログイン試行情報（キー: IPアドレス） */
    private final ConcurrentHashMap<String, AttemptData> loginAttempts = new ConcurrentHashMap<>();

    /** 新規登録試行情報（キー: IPアドレス） */
    private final ConcurrentHashMap<String, AttemptData> signupAttempts = new ConcurrentHashMap<>();

    /** パスワードリセット試行情報（キー: IPアドレス） */
    private final ConcurrentHashMap<String, AttemptData> passwordResetAttempts = new ConcurrentHashMap<>();

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
        loginAttempts.remove(ip);
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
        passwordResetAttempts.remove(ip);
    }

    /**
     * ブロック判定（ロック期限切れの場合はエントリを削除してfalseを返す）
     */
    private boolean isBlocked(ConcurrentHashMap<String, AttemptData> map, String key) {
        AttemptData data = map.get(key);
        if (data == null || data.lockedUntil() == null) {
            return false;
        }
        if (LocalDateTime.now().isBefore(data.lockedUntil())) {
            return true;
        }
        map.remove(key);
        return false;
    }

    /**
     * 試行を記録し、上限到達時はロックアウト時刻を設定する
     */
    private void record(ConcurrentHashMap<String, AttemptData> map, String key,
            int maxAttempts, long lockMinutes) {
        map.compute(key, (k, data) -> {
            int count = (data == null) ? 1 : data.count() + 1;
            LocalDateTime lockedUntil = count >= maxAttempts
                    ? LocalDateTime.now().plusMinutes(lockMinutes)
                    : null;
            return new AttemptData(count, lockedUntil);
        });
    }

    /**
     * 期限切れエントリの定期クリーンアップ（1時間ごと）
     */
    @Scheduled(fixedRate = 3_600_000)
    public void cleanUp() {
        LocalDateTime now = LocalDateTime.now();
        loginAttempts.entrySet().removeIf(e ->
                e.getValue().lockedUntil() != null && now.isAfter(e.getValue().lockedUntil()));
        signupAttempts.entrySet().removeIf(e ->
                e.getValue().lockedUntil() != null && now.isAfter(e.getValue().lockedUntil()));
        passwordResetAttempts.entrySet().removeIf(e ->
                e.getValue().lockedUntil() != null && now.isAfter(e.getValue().lockedUntil()));
    }
}
