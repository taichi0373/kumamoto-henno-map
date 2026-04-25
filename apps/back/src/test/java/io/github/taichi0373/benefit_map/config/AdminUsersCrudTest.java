package io.github.taichi0373.benefit_map.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import io.github.taichi0373.benefit_map.controller.admin.AdminUsersController;
import io.github.taichi0373.benefit_map.dto.admin.AdminUserResponseDto;
import io.github.taichi0373.benefit_map.repository.entity.UsersEntity;
import io.github.taichi0373.benefit_map.service.AuthService;
import io.github.taichi0373.benefit_map.service.admin.AdminUsersService;
import io.github.taichi0373.benefit_map.util.JwtUtil;

/**
 * ユーザー管理API 統合テスト
 * <p>
 * PUT /admin/users/{userId} のレスポンスに passwordHash が含まれないことを検証する。
 * </p>
 */
@WebMvcTest(AdminUsersController.class)
@Import(SecurityConfig.class)
class AdminUsersCrudTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private AuthService authService;

    @MockBean
    private AdminUsersService adminUsersService;

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
