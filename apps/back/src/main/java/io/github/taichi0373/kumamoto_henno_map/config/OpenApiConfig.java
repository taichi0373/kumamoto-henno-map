package io.github.taichi0373.kumamoto_henno_map.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger) 設定クラス
 * <p>
 * springdoc-openapi を使用したAPI仕様書の設定を行う。
 * JWT Bearer Token 認証スキーム（bearerAuth）を定義する。
 * Swagger UI: /kumamoto-henno-map/api/swagger-ui.html（dev プロファイル時のみ有効）
 * </p>
 */
@Configuration
public class OpenApiConfig {

    /**
     * OpenAPI 基本情報・セキュリティスキームの定義
     *
     * @return OpenAPI
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT認証トークン。POST /auth/login で取得し、Authorization: Bearer <token> ヘッダーに設定する。")))
                .info(new Info()
                        .title("くまもと自主返納特典マップ API")
                        .version("1.0.0")
                        .description("""
                                熊本県の運転免許自主返納者向け特典情報を提供する RESTful API。

                                ## 認証方式
                                JWT Bearer Token 認証を採用。
                                認証が必要なエンドポイントは事前に `POST /auth/login` でトークンを取得し、
                                `Authorization: Bearer <token>` ヘッダーに設定すること。
                                """));
    }
}
