package io.github.taichi0373.benefit_map.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger) 設定クラス
 * <p>
 * springdoc-openapi を使用したAPI仕様書の設定を行う。
 * JWT Cookie による認証スキームと API 基本情報を定義する。
 * Swagger UI: /benefit-map/api/swagger-ui.html
 * </p>
 */
@Configuration
@SecurityScheme(
        name = "cookieAuth",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.COOKIE,
        paramName = "jwt",
        description = "JWT認証Cookie。POST /auth/login で取得し、HttpOnly Cookie として自動送信される。"
)
public class OpenApiConfig {

    /**
     * OpenAPI 基本情報の定義
     *
     * @return OpenAPI
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("熊本県自主返納特典マップ API")
                        .version("1.0.0")
                        .description("""
                                熊本県の運転免許自主返納者向け特典情報を提供する RESTful API。

                                ## 認証方式
                                JWT を HttpOnly Cookie (`jwt`) で管理するステートレス認証を採用。
                                認証が必要なエンドポイントは事前に `POST /auth/login` でログインし、Cookie を取得すること。

                                ## CSRF 保護
                                状態変更系 (POST / PUT / DELETE) には `X-XSRF-TOKEN` ヘッダーが必要。
                                事前に `GET /auth/csrf` で CSRF トークンを取得すること。
                                除外対象: `/auth/**`、`POST /users/signup`
                                """));
    }
}
