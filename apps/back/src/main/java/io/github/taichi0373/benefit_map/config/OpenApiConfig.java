package io.github.taichi0373.benefit_map.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) 設定クラス
 * <p>
 * springdoc-openapi を使用したAPI仕様書の設定を行う。
 * JWT Cookie 認証スキーム（cookieAuth）と CSRF 対策カスタムヘッダースキーム（serviceHeader）を定義する。
 * Swagger UI: /benefit-map/api/swagger-ui.html（dev プロファイル時のみ有効）
 * </p>
 */
@Configuration
public class OpenApiConfig {

    /** 期待する X-Service-Name ヘッダー値（デフォルト: front、app.security.expected-service-name で変更可） */
    @Value("${app.security.expected-service-name:front}")
    private String expectedServiceName;

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
                        .addSecuritySchemes("serviceHeader", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-Service-Name")
                                .description("CSRF対策カスタムヘッダー。値は `" + expectedServiceName + "` を設定する（app.security.expected-service-name で変更可、デフォルト: front）。状態変更系エンドポイント（POST / PUT / PATCH / DELETE）で必須。")))
                .info(new Info()
                        .title("熊本県自主返納特典マップ API")
                        .version("1.0.0")
                        .description("""
                                熊本県の運転免許自主返納者向け特典情報を提供する RESTful API。

                                ## 認証方式
                                JWT を HttpOnly Cookie (`jwt`) で管理するステートレス認証を採用。
                                認証が必要なエンドポイントは事前に `POST /auth/login` でログインし、Cookie を取得すること。

                                ## CSRF 保護
                                状態変更系 (POST / PUT / PATCH / DELETE) には `X-Service-Name` ヘッダーが必要。
                                期待値は `app.security.expected-service-name` プロパティで設定する（デフォルト: front）。
                                サーバー側では Origin ヘッダーと X-Service-Name ヘッダーの両方を検証する。
                                除外対象: `/auth/**`、`POST /users/signup`
                                """));
    }

    /**
     * cookieAuth と serviceHeader が両方必要なエンドポイントの security 要件を AND 表現に変換する
     * <p>
     * OpenAPI の security 配列に別々に並べると OR 条件になるため、
     * 同一オブジェクト内にまとめることで「両方必須」の AND 条件として表現する。
     * 対象: PUT /users 等の状態変更系かつ認証必須のエンドポイント
     * </p>
     *
     * @return OperationCustomizer
     */
    @Bean
    public OperationCustomizer cookieAndServiceHeaderCombiner() {
        return (operation, handlerMethod) -> {
            var security = operation.getSecurity();
            if (security == null) return operation;

            boolean hasCookieAuth   = security.stream().anyMatch(r -> r.containsKey("cookieAuth"));
            boolean hasServiceHeader = security.stream().anyMatch(r -> r.containsKey("serviceHeader"));

            if (hasCookieAuth && hasServiceHeader) {
                // 別々の項目（OR）をひとつのオブジェクト（AND）にまとめる
                SecurityRequirement combined = new SecurityRequirement()
                        .addList("cookieAuth")
                        .addList("serviceHeader");
                operation.setSecurity(List.of(combined));
            }
            return operation;
        };
    }
}
