package com.example.back.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource)) // CorsConfigで定義されたBeanを使用
            .csrf(csrf -> csrf.disable()) // Vue.jsからのAPIアクセスのためCSRFを無効化
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin()) // H2コンソール用にSAME_ORIGINに変更
                .contentTypeOptions(contentTypeOptions -> {})
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000)
                )
            )
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/auth/health").permitAll()
                .requestMatchers("/route/**").permitAll()
                .requestMatchers("/location/**").permitAll()
                .requestMatchers("/benefit/search").permitAll()
                .requestMatchers("/benefit/transit").permitAll()
                .requestMatchers("/benefit/stores").permitAll()
                .requestMatchers("/benefit/municipality").permitAll()
                .requestMatchers("/h2-console/**").permitAll() // H2コンソールアクセスを許可
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
