package io.github.taichi0373.kumamoto_henno_map.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import io.github.taichi0373.kumamoto_henno_map.controller.AuthController;
import io.github.taichi0373.kumamoto_henno_map.exception.EmailNotVerifiedException;
import io.github.taichi0373.kumamoto_henno_map.service.AuthService;
import io.github.taichi0373.kumamoto_henno_map.service.EmailVerificationService;
import io.github.taichi0373.kumamoto_henno_map.service.EmailVerificationService.ConfirmResult;
import io.github.taichi0373.kumamoto_henno_map.service.LoginAttemptService;
import io.github.taichi0373.kumamoto_henno_map.service.PasswordResetService;
import io.github.taichi0373.kumamoto_henno_map.service.RefreshTokenService;
import io.github.taichi0373.kumamoto_henno_map.util.JwtUtil;

/**
 * メールアドレス確認関連APIの統合テスト
 * <p>
 * AuthController と SecurityConfig を組み合わせ、未認証で呼び出せることと各レスポンスを検証する。
 * </p>
 */
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = EmailVerificationApiTest.Config.class)
@TestPropertySource(properties = {
    "cors.allowed-origins=http://localhost:3000",
    "cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS",
    "cors.allowed-headers=*",
    "cors.allow-credentials=true"
})
class EmailVerificationApiTest {

    /** テスト用最小WebMVC設定 */
    @Configuration
    @EnableWebMvc
    @Import({SecurityConfig.class, CorsConfig.class, AuthController.class})
    static class Config {
    }

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private PasswordResetService passwordResetService;

    @MockitoBean
    private EmailVerificationService emailVerificationService;

    @MockitoBean
    private LoginAttemptService loginAttemptService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void メールアドレス未確認のログインは403を返し失敗として記録しない() throws Exception {
        when(authService.login(anyString(), anyString(), any()))
                .thenThrow(new EmailNotVerifiedException("メールアドレスの確認が完了していません"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"taro\",\"password\":\"password\"}"))
                .andExpect(status().isForbidden());
        verify(loginAttemptService, never()).recordLoginFailure(any());
    }

    @Test
    void 有効なトークンでの確認は200を返す() throws Exception {
        when(emailVerificationService.confirm("valid")).thenReturn(ConfirmResult.VERIFIED);

        mockMvc.perform(post("/auth/email-verification/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"valid\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void 無効なトークンでの確認は400を返し失敗として記録する() throws Exception {
        when(emailVerificationService.confirm("invalid")).thenReturn(ConfirmResult.INVALID);

        mockMvc.perform(post("/auth/email-verification/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"invalid\"}"))
                .andExpect(status().isBadRequest());
        verify(loginAttemptService).recordEmailVerificationFailure(any());
    }

    @Test
    void 変更後アドレスが使用済みの確認は409を返す() throws Exception {
        when(emailVerificationService.confirm("conflict")).thenReturn(ConfirmResult.CONFLICT);

        mockMvc.perform(post("/auth/email-verification/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"conflict\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void 再送は送信処理でエラーが発生しても200を返す() throws Exception {
        doThrow(new RuntimeException("送信エラー"))
                .when(emailVerificationService).resendSignupVerification("taro@example.com");

        mockMvc.perform(post("/auth/email-verification/resend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"taro@example.com\"}"))
                .andExpect(status().isOk());
        verify(loginAttemptService).recordEmailVerificationResendAttempt(any());
    }

    @Test
    void 再送の回数制限中は429を返す() throws Exception {
        when(loginAttemptService.isEmailVerificationResendBlocked(any())).thenReturn(true);

        mockMvc.perform(post("/auth/email-verification/resend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"taro@example.com\"}"))
                .andExpect(status().isTooManyRequests());
        verify(emailVerificationService, never()).resendSignupVerification(anyString());
    }
}
