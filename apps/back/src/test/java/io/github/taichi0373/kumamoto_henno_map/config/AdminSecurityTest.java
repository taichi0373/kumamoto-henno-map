package io.github.taichi0373.kumamoto_henno_map.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import io.github.taichi0373.kumamoto_henno_map.service.AuthService;
import io.github.taichi0373.kumamoto_henno_map.util.JwtUtil;

/**
 * SecurityConfig の /admin/** 保護に関する統合テスト
 * <p>
 * ROLE_USER が /admin/** へアクセスした場合に 403 と ApiResponseDto.error 形式の
 * レスポンスが返ることを検証する。
 * </p>
 */
@WebMvcTest(AdminTestController.class)
@Import(SecurityConfig.class)
class AdminSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private AuthService authService;

    /**
     * ROLE_USER が /admin/benefits にアクセスした場合に
     * 403 Forbidden と ApiResponseDto.error 形式のレスポンスが返ることを確認する
     */
    @Test
    @WithMockUser(roles = "USER")
    void 一般ユーザーがadminエンドポイントにアクセスすると403とApiResponseDtoエラー形式が返る() throws Exception {
        mockMvc.perform(get("/admin/benefits"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("アクセス権限がありません"));
    }

    /**
     * 未認証ユーザーが /admin/benefits にアクセスした場合に 401 が返ることを確認する
     */
    @Test
    void 未認証ユーザーがadminエンドポイントにアクセスすると401が返る() throws Exception {
        mockMvc.perform(get("/admin/benefits"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * ROLE_ADMIN を持つユーザーが /admin/benefits にアクセスした場合に 200 が返ることを確認する
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void 管理者ユーザーがadminエンドポイントにアクセスすると200が返る() throws Exception {
        mockMvc.perform(get("/admin/benefits"))
                .andExpect(status().isOk());
    }
}
