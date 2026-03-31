package io.github.taichi0373.benefit_map.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * パスワードエンコーダー設定クラス
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * BCryptパスワードエンコーダーBean
     * @return BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
