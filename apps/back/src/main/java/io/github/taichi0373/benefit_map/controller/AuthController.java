package io.github.taichi0373.benefit_map.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.taichi0373.benefit_map.dto.ApiResponseDto;
import io.github.taichi0373.benefit_map.dto.LoginRequestDto;
import io.github.taichi0373.benefit_map.dto.LoginResponseDto;
import io.github.taichi0373.benefit_map.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 認証コントローラー
 * <p>
 * JWT認証のエンドポイントを提供する。
 * </p>
 */
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
    @Value("${app.security.cookie.secure:false}")
    private boolean cookieSecure;

    /**
     * ログイン
     * <p>
     * 認証成功時に JWT を HttpOnly Cookie にセットし、ユーザー情報を返す。
     * </p>
     * @param request ログインリクエスト
     * @param httpResponse HTTPレスポンス
     * @return ユーザー情報（トークンはCookieに格納）
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<?>> login(
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
                    .path("/")
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
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse httpResponse) {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.noContent().build();
    }
}
