package io.github.taichi0373.benefit_map.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import io.github.taichi0373.benefit_map.security.JwtAuthenticationFilter;
import io.github.taichi0373.benefit_map.util.JwtUtil;

/**
 * Spring Security 設定クラス
 * <p>
 * JWT認証を使用したステートレスセキュリティ設定を行う。
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

    /**
     * JWT認証フィルターBean
     * @return JwtAuthenticationFilter
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService);
    }

    /**
     * セキュリティフィルターチェーンの設定
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception 設定エラー時
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/users/signup").permitAll()
                .requestMatchers("/users/**").authenticated()
                .anyRequest().permitAll()
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
