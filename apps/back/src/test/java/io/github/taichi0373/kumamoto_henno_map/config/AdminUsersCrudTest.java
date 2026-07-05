package io.github.taichi0373.kumamoto_henno_map.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
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

import io.github.taichi0373.kumamoto_henno_map.controller.admin.AdminUsersController;
import io.github.taichi0373.kumamoto_henno_map.dto.admin.AdminUserResponseDto;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.UsersEntity;
import io.github.taichi0373.kumamoto_henno_map.service.AuthService;
import io.github.taichi0373.kumamoto_henno_map.service.admin.AdminUsersService;
import io.github.taichi0373.kumamoto_henno_map.util.JwtUtil;

/**
 * ユーザー管理API 統合テスト
 * <p>
 * PUT /admin/users/{userId} のレスポンスに passwordHash が含まれないことを検証する。
 * </p>
 */
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = AdminUsersCrudTest.Config.class)
@TestPropertySource(properties = {
    "cors.allowed-origins=http://localhost:3000",
    "cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS",
    "cors.allowed-headers=*",
    "cors.allow-credentials=true"
})
class AdminUsersCrudTest {

    /** テスト用最小WebMVC設定 */
    @Configuration
    @EnableWebMvc
    @Import({SecurityConfig.class, CorsConfig.class, AdminUsersController.class})
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
    private AdminUsersService adminUsersService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    /**
     * PUT /admin/users/{userId} のレスポンスに passwordHash フィールドが含まれないことを確認する
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void ユーザー更新レスポンスにpasswordHashが含まれない() throws Exception {
        var dto = new AdminUserResponseDto();
        dto.setUserId(1L);
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");

        when(adminUsersService.update(eq(1L), any(UsersEntity.class))).thenReturn(dto);

        mockMvc.perform(put("/admin/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"email\":\"test@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.passwordHash").doesNotExist());
    }
}
