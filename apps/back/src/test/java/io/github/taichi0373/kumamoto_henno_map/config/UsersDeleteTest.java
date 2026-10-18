package io.github.taichi0373.kumamoto_henno_map.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

import io.github.taichi0373.kumamoto_henno_map.constants.CodeConstants;
import io.github.taichi0373.kumamoto_henno_map.controller.UsersController;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.UsersEntity;
import io.github.taichi0373.kumamoto_henno_map.security.CustomUserDetails;
import io.github.taichi0373.kumamoto_henno_map.service.AuthService;
import io.github.taichi0373.kumamoto_henno_map.service.LoginAttemptService;
import io.github.taichi0373.kumamoto_henno_map.service.UsersService;
import io.github.taichi0373.kumamoto_henno_map.util.JwtUtil;

/**
 * DELETE /users エンドポイントのテスト
 * <p>
 * ユーザー自身によるアカウント削除APIの認証・認可・ビジネスロジック分岐を検証する。
 * </p>
 */
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = UsersDeleteTest.Config.class)
@TestPropertySource(properties = {
    "cors.allowed-origins=http://localhost:3000",
    "cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS",
    "cors.allowed-headers=*",
    "cors.allow-credentials=true"
})
class UsersDeleteTest {

    /** テスト用最小WebMVC設定 */
    @Configuration
    @EnableWebMvc
    @Import({SecurityConfig.class, CorsConfig.class, UsersController.class})
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
    private UsersService usersService;

    @MockitoBean
    private LoginAttemptService loginAttemptService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    /** テスト用 CustomUserDetails を生成するヘルパー */
    private CustomUserDetails buildUserDetails(Long userId, String isAdmin) {
        UsersEntity entity = new UsersEntity();
        entity.setUserId(userId);
        entity.setIsAdmin(isAdmin);
        entity.setUsername("testuser");
        entity.setPasswordHash("hash");
        return new CustomUserDetails(entity);
    }

    /** 正常削除: パスワード一致 → 200 */
    @Test
    void deleteAccount_正常削除_200() throws Exception {
        when(usersService.deleteOwnAccount(eq(1L), any())).thenReturn(true);

        mockMvc.perform(delete("/users")
                .with(user(buildUserDetails(1L, CodeConstants.UserType.GENERAL)))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\":\"pass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    /** パスワード不一致 → 400 */
    @Test
    void deleteAccount_パスワード不一致_400() throws Exception {
        when(usersService.deleteOwnAccount(eq(1L), any())).thenReturn(false);

        mockMvc.perform(delete("/users")
                .with(user(buildUserDetails(1L, CodeConstants.UserType.GENERAL)))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\":\"wrong\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("パスワードが正しくありません"));
    }

    /** 未認証（Authorizationヘッダーなし） → 401 */
    @Test
    void deleteAccount_未認証_401() throws Exception {
        mockMvc.perform(delete("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\":\"pass\"}"))
                .andExpect(status().isUnauthorized());
    }

    /** 管理者アカウント → 400 */
    @Test
    void deleteAccount_管理者_400() throws Exception {
        when(usersService.deleteOwnAccount(eq(1L), any()))
                .thenThrow(new IllegalStateException("管理者アカウントは退会できません"));

        mockMvc.perform(delete("/users")
                .with(user(buildUserDetails(1L, CodeConstants.UserType.ADMIN)))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\":\"pass\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("管理者アカウントは退会できません"));
    }

    /** ユーザーが存在しない → 404 */
    @Test
    void deleteAccount_ユーザー不存在_404() throws Exception {
        when(usersService.deleteOwnAccount(eq(1L), any())).thenReturn(null);

        mockMvc.perform(delete("/users")
                .with(user(buildUserDetails(1L, CodeConstants.UserType.GENERAL)))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\":\"pass\"}"))
                .andExpect(status().isNotFound());
    }
}
