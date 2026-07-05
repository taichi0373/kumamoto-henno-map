package io.github.taichi0373.kumamoto_henno_map.config;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
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

import io.github.taichi0373.kumamoto_henno_map.service.AuthService;
import io.github.taichi0373.kumamoto_henno_map.util.JwtUtil;

/**
 * SecurityConfig の /admin/** 保護に関する統合テスト
 * <p>
 * ROLE_USER が /admin/** へアクセスした場合に 403 と ApiResponseDto.error 形式の
 * レスポンスが返ることを検証する。
 * </p>
 */
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = AdminSecurityTest.Config.class)
@TestPropertySource(properties = {
    "cors.allowed-origins=http://localhost:3000",
    "cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS",
    "cors.allowed-headers=*",
    "cors.allow-credentials=true"
})
class AdminSecurityTest {

    /** テスト用最小WebMVC設定 */
    @Configuration
    @EnableWebMvc
    @Import({SecurityConfig.class, CorsConfig.class, AdminTestController.class})
    static class Config {
    }

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private AuthService authService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

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
