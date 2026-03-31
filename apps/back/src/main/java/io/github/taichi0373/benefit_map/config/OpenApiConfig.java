package io.github.taichi0373.benefit_map.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) 設定クラス
 * <p>
 * springdoc-openapi を使用したAPI仕様書の設定を行う。
 * JWT Cookie 認証スキーム（cookieAuth）と CSRF トークンスキーム（csrfToken）を定義する。
 * Swagger UI: /benefit-map/api/swagger-ui.html（dev プロファイル時のみ有効）
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
                        .addSecuritySchemes("cookieAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .name("jwt")
                                .description("JWT認証Cookie。POST /auth/login で取得し、HttpOnly Cookie として自動送信される。"))
                        .addSecuritySchemes("csrfToken", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-XSRF-TOKEN")
                                .description("CSRFトークン。GET /auth/csrf で取得した値を X-XSRF-TOKEN ヘッダーに設定する。POST /benefit/search・PUT /users・POST /route/search で必須。")))
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

    /**
     * cookieAuth と csrfToken が両方必要なエンドポイントの security 要件を AND 表現に変換する
     * <p>
     * OpenAPI の security 配列に別々に並べると OR 条件になるため、
     * 同一オブジェクト内にまとめることで「両方必須」の AND 条件として表現する。
     * 対象: PUT /users
     * </p>
     *
     * @return OperationCustomizer
     */
    @Bean
    public OperationCustomizer cookieAndCsrfCombiner() {
        return (operation, handlerMethod) -> {
            var security = operation.getSecurity();
            if (security == null) return operation;

            boolean hasCookieAuth = security.stream().anyMatch(r -> r.containsKey("cookieAuth"));
            boolean hasCsrfToken  = security.stream().anyMatch(r -> r.containsKey("csrfToken"));

            if (hasCookieAuth && hasCsrfToken) {
                // 別々の項目（OR）をひとつのオブジェクト（AND）にまとめる
                SecurityRequirement combined = new SecurityRequirement()
                        .addList("cookieAuth")
                        .addList("csrfToken");
                operation.setSecurity(List.of(combined));
            }
            return operation;
        };
    }
}
