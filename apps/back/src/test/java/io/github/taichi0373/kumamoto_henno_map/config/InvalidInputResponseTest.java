package io.github.taichi0373.kumamoto_henno_map.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
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

import io.github.taichi0373.kumamoto_henno_map.controller.RouteController;
import io.github.taichi0373.kumamoto_henno_map.controller.admin.AdminBenefitController;
import io.github.taichi0373.kumamoto_henno_map.service.AuthService;
import io.github.taichi0373.kumamoto_henno_map.service.RouteService;
import io.github.taichi0373.kumamoto_henno_map.service.admin.AdminBenefitService;
import io.github.taichi0373.kumamoto_henno_map.service.admin.GeocodingService;
import io.github.taichi0373.kumamoto_henno_map.util.JwtUtil;

/**
 * 不正な入力に対するレスポンスの統合テスト
 * <p>
 * Controller と SecurityConfig を組み合わせ、不正な入力が 400 / 409 になることを検証する。
 * </p>
 */
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = InvalidInputResponseTest.Config.class)
@TestPropertySource(properties = {
    "cors.allowed-origins=http://localhost:3000",
    "cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS",
    "cors.allowed-headers=*",
    "cors.allow-credentials=true"
})
class InvalidInputResponseTest {

    /** テスト用最小WebMVC設定 */
    @Configuration
    @EnableWebMvc
    @Import({SecurityConfig.class, CorsConfig.class, AdminBenefitController.class, RouteController.class})
    static class Config {
    }

    private static final String SQL_TEXT = "insert into benefit (benefit_id) values ('x')";

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

    @MockitoBean
    private RouteService routeService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 特典登録の重複は409を返しSQL文を含まない() throws Exception {
        when(adminBenefitService.create(any()))
                .thenThrow(new DuplicateKeyException(SQL_TEXT, new SQLException(SQL_TEXT, "23505")));

        mockMvc.perform(post("/admin/benefits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"benefitId\":\"x\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("既に登録されています"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 特典登録の文字数超過は400を返す() throws Exception {
        when(adminBenefitService.create(any()))
                .thenThrow(new DataIntegrityViolationException(SQL_TEXT, new SQLException(SQL_TEXT, "22001")));

        mockMvc.perform(post("/admin/benefits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"benefitId\":\"x\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void クエリのNUL文字は400を返す() throws Exception {
        mockMvc.perform(get("/admin/benefits").param("municipalityCd", "a\0b"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verifyNoInteractions(adminBenefitService);
    }

    @Test
    void 不正な経路探索リクエストは400を返しOTPを呼ばない() throws Exception {
        mockMvc.perform(post("/route/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"transportMode\":\"transportMode\",\"startLat\":1.2,\"startLon\":1.2,\"endLat\":1.2,\"endLon\":1.2}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("交通手段が正しくありません"));

        verifyNoInteractions(routeService);
    }
}
