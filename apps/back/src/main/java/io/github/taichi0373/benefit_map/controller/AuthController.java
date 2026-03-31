package io.github.taichi0373.benefit_map.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.web.csrf.CsrfToken;

import io.github.taichi0373.benefit_map.dto.ApiResponseDto;
import io.github.taichi0373.benefit_map.dto.LoginRequestDto;
import io.github.taichi0373.benefit_map.dto.LoginResponseDto;
import io.github.taichi0373.benefit_map.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 認証コントローラー
 * <p>
 * JWT認証のエンドポイントを提供する。
 * </p>
 */
@Tag(name = "認証", description = "ログイン・ログアウト・CSRFトークン取得")
@RestController
@RequestMapping("/auth")
public class AuthController {

    /** 認証サービス */
    @Autowired
    private AuthService authService;

    /** トークン有効期限（ミリ秒） */
    @Value("${jwt.expiration:3600000}")
    private long jwtExpiration;

    /** CookieのSecure属性設定 */
    @Value("${app.security.cookie.secure:true}")
    private boolean cookieSecure;

    /** CookieのPath */
    @Value("${server.servlet.context-path:/benefit-map/api}")
    private String contextPath;

    /**
     * ログイン
     * <p>
     * 認証成功時に JWT を HttpOnly Cookie にセットし、ユーザー情報を返す。
     * </p>
     * @param request ログインリクエスト
     * @param httpResponse HTTPレスポンス
     * @return ユーザー情報（トークンはCookieに格納）
     */
    @Operation(summary = "ログイン", description = "ユーザー名とパスワードで認証し、JWT を HttpOnly Cookie (`jwt`) にセットする。CSRF 保護は不要。")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ログイン成功（data: LoginResponseDto）"),
            @ApiResponse(responseCode = "401", description = "ユーザー名またはパスワードが正しくない",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "サーバー内部エラー",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> login(
            @RequestBody LoginRequestDto request,
            HttpServletResponse httpResponse) {
        try {
            LoginResponseDto response = authService.login(request.getUsername(), request.getPassword());
            if (response == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDto.error("ユーザー名またはパスワードが正しくありません"));
            }
            ResponseCookie cookie = ResponseCookie.from("jwt", response.getToken())
                    .httpOnly(true)
                    .secure(cookieSecure)
                    .sameSite("Strict")
                    .path(contextPath)
                    .maxAge(jwtExpiration / 1000)
                    .build();
            httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            return ResponseEntity.ok(ApiResponseDto.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("ログイン中にエラーが発生しました"));
        }
    }

    /**
     * ログアウト
     * <p>
     * JWT Cookie を削除する。削除操作のため 204 No Content を返す。
     * </p>
     * @param httpResponse HTTPレスポンス
     * @return 204 No Content
     */
    @Operation(summary = "ログアウト", description = "JWT Cookie を無効化（Max-Age=0）する。CSRF 保護は不要。")
    @ApiResponse(responseCode = "204", description = "ログアウト成功（ボディなし）")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse httpResponse) {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .path(contextPath)
                .maxAge(0)
                .build();
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.noContent().build();
    }

    /**
     * CSRF トークン取得
     * <p>
     * フロントエンドが状態変更系API実行前に取得するCSRFトークンを提供する。
     * CookieCsrfTokenRepository により自動的にXSRF-TOKENクッキーも設定される。
     * </p>
     * @param csrfToken Spring Security により自動注入されるCSRFトークン
     * @return CSRFトークン文字列
     */
    @Operation(summary = "CSRFトークン取得", description = "状態変更系 API を呼び出す前に取得する。レスポンスと同時に `XSRF-TOKEN` Cookie も設定される。")
    @ApiResponse(responseCode = "200", description = "CSRFトークン取得成功",
            content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    @GetMapping("/csrf")
    public ResponseEntity<ApiResponseDto<String>> getCsrfToken(CsrfToken csrfToken) {
        return ResponseEntity.ok(ApiResponseDto.success(csrfToken.getToken()));
    }
}
