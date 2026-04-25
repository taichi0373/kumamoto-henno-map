package io.github.taichi0373.benefit_map.config;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import io.github.taichi0373.benefit_map.controller.admin.AdminBenefitController;
import io.github.taichi0373.benefit_map.service.AuthService;
import io.github.taichi0373.benefit_map.service.admin.AdminBenefitService;
import io.github.taichi0373.benefit_map.util.JwtUtil;

/**
 * 特典管理API 認可・依存チェック統合テスト
 * <p>
 * ROLE_ADMIN での GET 200、ROLE_USER での GET 403、未認証での 401、
 * および依存レコードあり DELETE の 409 を検証する。
 * </p>
 */
@WebMvcTest(AdminBenefitController.class)
@Import(SecurityConfig.class)
class AdminBenefitCrudTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private AuthService authService;

    @MockBean
    private AdminBenefitService adminBenefitService;

    /**
     * ROLE_ADMIN で GET /admin/benefits にアクセスすると 200 OK が返ることを確認する
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void 管理者ユーザーが特典一覧を取得すると200が返る() throws Exception {
        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/benefits"))
                .andExpect(status().isOk());
    }

    /**
     * ROLE_USER で GET /admin/benefits にアクセスすると 403 Forbidden が返ることを確認する
     */
    @Test
    @WithMockUser(roles = "USER")
    void 一般ユーザーが特典一覧にアクセスすると403が返る() throws Exception {
        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/benefits"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    /**
     * 未認証で GET /admin/benefits にアクセスすると 401 Unauthorized が返ることを確認する
     */
    @Test
    void 未認証ユーザーが特典一覧にアクセスすると401が返る() throws Exception {
        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/benefits"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * ROLE_ADMIN で依存レコードありの DELETE /admin/benefits/{id} を実行すると
     * 409 Conflict と ApiResponseDto.error 形式のレスポンスが返ることを確認する
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void 依存レコードがある特典を削除すると409が返る() throws Exception {
        doThrow(new IllegalStateException("この特典に紐付く特典条件が存在するため削除できません"))
                .when(adminBenefitService).delete(anyString());

        mockMvc.perform(delete("/admin/benefits/benefit_with_deps"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("この特典に紐付く特典条件が存在するため削除できません"));
    }
}
