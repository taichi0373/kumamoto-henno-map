package io.github.taichi0373.kumamoto_henno_map.config;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import io.github.taichi0373.kumamoto_henno_map.controller.admin.AdminBenefitController;
import io.github.taichi0373.kumamoto_henno_map.service.AuthService;
import io.github.taichi0373.kumamoto_henno_map.service.admin.GeocodingService;
import io.github.taichi0373.kumamoto_henno_map.service.admin.AdminBenefitService;
import io.github.taichi0373.kumamoto_henno_map.util.JwtUtil;

/**
 * 特典管理API 認可・依存チェック統合テスト
 * <p>
 * ROLE_ADMIN での GET 200、ROLE_USER での GET 403、未認証での 401、
 * および依存レコードあり DELETE の 409 を検証する。
 * </p>
 */
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = AdminBenefitCrudTest.Config.class)
@TestPropertySource(properties = {
    "cors.allowed-origins=http://localhost:3000",
    "cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS",
    "cors.allowed-headers=*",
    "cors.allow-credentials=true"
})
class AdminBenefitCrudTest {

    /** テスト用最小WebMVC設定 */
    @Configuration
    @EnableWebMvc
    @Import({SecurityConfig.class, CorsConfig.class, AdminBenefitController.class})
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
    private AdminBenefitService adminBenefitService;

    @MockitoBean
    private GeocodingService geocodingService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    /**
     * ROLE_ADMIN で GET /admin/benefits にアクセスすると 200 OK が返ることを確認する
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void 管理者ユーザーが特典一覧を取得すると200が返る() throws Exception {
        mockMvc.perform(get("/admin/benefits"))
                .andExpect(status().isOk());
    }

    /**
     * ROLE_USER で GET /admin/benefits にアクセスすると 403 Forbidden が返ることを確認する
     */
    @Test
    @WithMockUser(roles = "USER")
    void 一般ユーザーが特典一覧にアクセスすると403が返る() throws Exception {
        mockMvc.perform(get("/admin/benefits"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    /**
     * 未認証で GET /admin/benefits にアクセスすると 401 Unauthorized が返ることを確認する
     */
    @Test
    void 未認証ユーザーが特典一覧にアクセスすると401が返る() throws Exception {
        mockMvc.perform(get("/admin/benefits"))
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
