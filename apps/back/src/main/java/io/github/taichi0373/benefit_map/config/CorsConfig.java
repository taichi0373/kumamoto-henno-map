package io.github.taichi0373.benefit_map.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * CORS設定クラス
 * <p>
 * クロスオリジンリソース共有（CORS）の許可設定を行う。
 * application.properties の cors.* プロパティを読み込んで設定する。
 * cors.allowed-origins が空またはブランクのみの場合は起動時に例外をスローする（fail-fast）。
 * </p>
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins}")
    private String allowedOriginsStr;

    @Value("${cors.allowed-methods}")
    private String allowedMethods;

    @Value("${cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${cors.allow-credentials}")
    private boolean allowCredentials;

    /** パース済みの許可オリジンリスト */
    private List<String> parsedAllowedOrigins;

    /**
     * パース済みの許可オリジンリストを返す。
     * <p>
     * {@link io.github.taichi0373.benefit_map.security.CsrfProtectionFilter} 等、
     * 同じ許可オリジンリストを必要とするコンポーネントから利用する。
     * {@code @PostConstruct} の実行後に呼び出すこと。
     * </p>
     *
     * @return trim・空除外済みの許可オリジンリスト
     */
    public List<String> getParsedAllowedOrigins() {
        return parsedAllowedOrigins;
    }

    /**
     * 起動時にCORS設定の妥当性を検証する。
     * <p>
     * cors.allowed-origins が未設定・空・ブランクのみの場合、
     * 状態変更系リクエストがすべて 403 で拒否されてしまうため、
     * 設定不備を即座に検出して起動を失敗させる。
     * </p>
     *
     * @throws IllegalStateException cors.allowed-origins が空の場合
     */
    @PostConstruct
    public void validate() {
        parsedAllowedOrigins = Arrays.stream(allowedOriginsStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        if (parsedAllowedOrigins.isEmpty()) {
            throw new IllegalStateException(
                    "cors.allowed-origins が空または未設定です。" +
                    "application.properties に有効なオリジンを設定してください。 " +
                    "(例: cors.allowed-origins=http://localhost:3000)"
            );
        }
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(parsedAllowedOrigins.toArray(String[]::new))
                .allowedMethods(allowedMethods.split(","))
                .allowedHeaders(allowedHeaders.split(","))
                .allowCredentials(allowCredentials)
                .maxAge(3600);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(parsedAllowedOrigins);
        configuration.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));
        configuration.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));
        configuration.setAllowCredentials(allowCredentials);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
