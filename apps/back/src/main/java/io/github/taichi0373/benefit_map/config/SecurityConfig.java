package io.github.taichi0373.benefit_map.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.taichi0373.benefit_map.dto.ApiResponseDto;
import io.github.taichi0373.benefit_map.security.CsrfProtectionFilter;
import io.github.taichi0373.benefit_map.security.JwtAuthenticationFilter;
import io.github.taichi0373.benefit_map.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

/**
 * Spring Security 設定クラス
 * <p>
 * JWT認証を使用したステートレスセキュリティ設定を行う。
 * フォームログイン・HTTP Basicは無効化し、Spring標準CSRF保護は無効化する。
 * JWT は HttpOnly Cookie で送信するため、Origin検証＋X-Service-Nameカスタムヘッダー検証で
 * CSRF対策を行う（{@link CsrfProtectionFilter}）。
 * 認証エラー時は {@link ApiResponseDto} 形式のJSONを返す。
 * </p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** JWTユーティリティ */
    @Autowired
    private JwtUtil jwtUtil;

    /** ユーザー詳細サービス（AuthServiceが注入される） */
    @Autowired
    private UserDetailsService userDetailsService;

    /** CORS設定ソース */
    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    /** JSONシリアライザー */
    @Autowired
    private ObjectMapper objectMapper;

    /** 許可するOriginのカンマ区切り文字列 */
    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    /** 期待するX-Service-Nameヘッダー値 */
    @Value("${app.security.expected-service-name:front}")
    private String expectedServiceName;

    /**
     * JWT認証フィルターBean
     *
     * @return JwtAuthenticationFilter
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService);
    }

    /**
     * CSRFカスタム対策フィルターBean
     * <p>
     * Origin ヘッダーと X-Service-Name カスタムヘッダーを検証し、
     * 不正なリクエストを HTTP 403 で拒否する。
     * </p>
     *
     * @return CsrfProtectionFilter
     */
    @Bean
    public CsrfProtectionFilter csrfProtectionFilter() {
        return new CsrfProtectionFilter(allowedOrigins, expectedServiceName, objectMapper);
    }

    /**
     * セキュリティフィルターチェーンの設定
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception 設定エラー時
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // JWT は Authorization ヘッダーで送信するためCSRF保護は不要
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/users/signup").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/users/**").authenticated()
                .requestMatchers("/benefit/users/**").authenticated()
                .anyRequest().permitAll()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->
                    writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "認証が必要です"))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                    writeJsonError(response, HttpServletResponse.SC_FORBIDDEN, "アクセス権限がありません"))
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(csrfProtectionFilter(), JwtAuthenticationFilter.class);

        return http.build();
    }

    /**
     * エラー内容をJSON形式でレスポンスに書き込む
     *
     * @param response HTTPレスポンス
     * @param status   HTTPステータスコード
     * @param message  エラーメッセージ
     * @throws IOException 書き込みエラー時
     */
    private void writeJsonError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getOutputStream(), ApiResponseDto.error(message));
    }
}
