package io.github.taichi0373.kumamoto_henno_map.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** LoginAttemptService のログイン試行制限のテスト */
class LoginAttemptServiceTest {

    /** ログインの最大失敗試行回数（LoginAttemptService.LOGIN_MAX_ATTEMPTS と一致させる） */
    private static final int LOGIN_MAX_ATTEMPTS = 10;

    private static final String IP = "192.0.2.1";
    private static final String OTHER_IP = "192.0.2.2";

    private LoginAttemptService loginAttemptService;

    @BeforeEach
    void setUp() {
        loginAttemptService = new LoginAttemptService();
    }

    private void failLogin(String ip, int times) {
        for (int i = 0; i < times; i++) {
            loginAttemptService.recordLoginFailure(ip);
        }
    }

    @Test
    void 試行記録がないときブロックされない() {
        assertThat(loginAttemptService.isLoginBlocked(IP)).isFalse();
    }

    @Test
    void 上限未満の失敗ではブロックされない() {
        failLogin(IP, LOGIN_MAX_ATTEMPTS - 1);

        assertThat(loginAttemptService.isLoginBlocked(IP)).isFalse();
    }

    @Test
    void 上限回数の失敗でブロックされる() {
        failLogin(IP, LOGIN_MAX_ATTEMPTS);

        assertThat(loginAttemptService.isLoginBlocked(IP)).isTrue();
    }

    @Test
    void ブロックはIPアドレス単位で他のIPには影響しない() {
        failLogin(IP, LOGIN_MAX_ATTEMPTS);

        assertThat(loginAttemptService.isLoginBlocked(OTHER_IP)).isFalse();
    }

    @Test
    void ログイン成功で失敗カウントがリセットされる() {
        failLogin(IP, LOGIN_MAX_ATTEMPTS - 1);
        loginAttemptService.recordLoginSuccess(IP);
        failLogin(IP, LOGIN_MAX_ATTEMPTS - 1);

        assertThat(loginAttemptService.isLoginBlocked(IP)).isFalse();
    }
}
