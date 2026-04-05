package io.github.taichi0373.benefit_map.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
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
import io.github.taichi0373.benefit_map.security.JwtAuthenticationFilter;
import io.github.taichi0373.benefit_map.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Spring Security 設定クラス
 * <p>
 * JWT認証を使用したステートレスセキュリティ設定を行う。
 * フォームログイン・HTTP Basic・CSRFは無効化し、
 * Authorization: Bearer ヘッダーによるJWT認証を使用する。
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
     * セキュリティフィルターチェーンの設定
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception 設定エラー時
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF無効化（STATELESSなBearer Token認証では不要）
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(authz -> authz
                // /auth/** は全て permitAll（login・refresh・logout・パスワードリセット）
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
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

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
