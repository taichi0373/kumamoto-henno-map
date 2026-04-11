package io.github.taichi0373.benefit_map.controller;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import io.github.taichi0373.benefit_map.dto.PasswordResetConfirmRequestDto;
import io.github.taichi0373.benefit_map.dto.PasswordResetRequestDto;
import io.github.taichi0373.benefit_map.dto.RefreshResponseDto;
import io.github.taichi0373.benefit_map.service.AuthService;
import io.github.taichi0373.benefit_map.service.LoginAttemptService;
import io.github.taichi0373.benefit_map.service.PasswordResetService;
import io.github.taichi0373.benefit_map.service.RefreshTokenService;
import io.github.taichi0373.benefit_map.service.RefreshTokenService.RotationResult;
import io.github.taichi0373.benefit_map.util.RequestUtils;
import io.github.taichi0373.benefit_map.util.ValidateUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 認証コントローラー
 * <p>
 * JWT認証のエンドポイントを提供する。
 * ログイン成功時はアクセストークン（レスポンスボディ）と
 * リフレッシュトークン（HttpOnly Cookie）を発行する。
 * クライアントはアクセストークンをメモリのみに保持し、
 * ページリフレッシュ時は /auth/refresh でアクセストークンを再取得する。
 * </p>
 */
@Tag(name = "認証", description = "ログイン・ログアウト・トークンリフレッシュ")
@RestController
@RequestMapping("/auth")
public class AuthController {

    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    /** リフレッシュトークンCookie名 */
    private static final String REFRESH_COOKIE_NAME = "refresh_token";

    /** 認証サービス */
    @Autowired
    private AuthService authService;

    /** リフレッシュトークンサービス */
    @Autowired
    private RefreshTokenService refreshTokenService;

    /** パスワードリセットサービス */
    @Autowired
    private PasswordResetService passwordResetService;

    /** ログイン試行管理サービス */
    @Autowired
    private LoginAttemptService loginAttemptService;

    /** リフレッシュトークン有効期限（秒） */
    @Value("${jwt.refresh-expiration:2592000}")
    private long refreshExpirationSeconds;

    /** CookieのSecure属性（本番はtrue、開発はfalse） */
    @Value("${app.security.cookie.secure:true}")
    private boolean cookieSecure;

    /** APIコンテキストパス */
    @Value("${server.servlet.context-path:/benefit-map/api}")
    private String contextPath;

    /**
     * ログイン
     * <p>
     * 認証成功時にアクセストークン（レスポンスボディ）と
     * リフレッシュトークン（HttpOnly Cookie）を発行する。
     * </p>
     * @param request ログインリクエスト
     * @param httpRequest HTTPリクエスト
     * @param httpResponse HTTPレスポンス
     * @return アクセストークンとユーザー情報
     */
    @Operation(summary = "ログイン",
            description = "認証成功時にアクセストークン（レスポンスボディ）とリフレッシュトークン（HttpOnly Cookie）を発行する。")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ログイン成功（data: LoginResponseDto）"),
            @ApiResponse(responseCode = "401", description = "ユーザー名またはパスワードが正しくない",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "429", description = "ログイン試行回数が上限を超えた（15分後に解除）",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "サーバー内部エラー",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> login(
            @RequestBody LoginRequestDto request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        String clientIp = RequestUtils.getClientIp(httpRequest);
        if (loginAttemptService.isLoginBlocked(clientIp)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(ApiResponseDto.error("ログイン試行回数が上限を超えました。しばらく時間をおいて再度お試しください。"));
        }
        try {
            LoginResponseDto response = authService.login(request.getUsername(), request.getPassword());
            if (response == null) {
                loginAttemptService.recordLoginFailure(clientIp);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDto.error("ユーザー名またはパスワードが正しくありません"));
            }
            loginAttemptService.recordLoginSuccess(clientIp);

            // リフレッシュトークンを生成して HttpOnly Cookie にセット
            String refreshToken = refreshTokenService.createRefreshToken(response.getUserId());
            setRefreshTokenCookie(httpResponse, refreshToken);

            return ResponseEntity.ok(ApiResponseDto.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("ログイン中にエラーが発生しました"));
        }
    }

    /**
     * トークンリフレッシュ
     * <p>
     * HttpOnly Cookieのリフレッシュトークンでアクセストークンとリフレッシュトークンを再発行する。
     * ページリフレッシュ時のセッション復元に使用する。
     * リフレッシュトークンはローテーションされるため、旧トークンは即時失効する。
     * </p>
     * @param httpRequest HTTPリクエスト
     * @param httpResponse HTTPレスポンス
     * @return 新しいアクセストークン
     */
    @Operation(summary = "トークンリフレッシュ",
            description = "HttpOnly Cookieのリフレッシュトークンでアクセストークンとリフレッシュトークンを再発行する。ページリフレッシュ時のセッション復元に使用する。")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "リフレッシュ成功（data: RefreshResponseDto）"),
            @ApiResponse(responseCode = "401", description = "リフレッシュトークンが無効または期限切れ",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponseDto<RefreshResponseDto>> refresh(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        String plainToken = extractRefreshTokenFromCookie(httpRequest);
        if (plainToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDto.error("リフレッシュトークンがありません"));
        }

        RotationResult result = refreshTokenService.validateAndRotate(plainToken);
        if (result == null) {
            // 旧Cookieをクリア
            clearRefreshTokenCookie(httpResponse);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDto.error("リフレッシュトークンが無効または期限切れです。再ログインしてください。"));
        }

        // 新しいアクセストークンを生成
        String newAccessToken = authService.generateAccessToken(result.userId());

        // ローテーションされた新リフレッシュトークンをCookieにセット
        setRefreshTokenCookie(httpResponse, result.newPlainToken());

        return ResponseEntity.ok(ApiResponseDto.success(new RefreshResponseDto(newAccessToken)));
    }

    /**
     * ログアウト
     * <p>
     * DBのリフレッシュトークンを失効させ、Cookieをクリアする。
     * アクセストークンはクライアント側でメモリから削除する。
     * </p>
     * @param httpRequest HTTPリクエスト
     * @param httpResponse HTTPレスポンス
     * @return 204 No Content
     */
    @Operation(summary = "ログアウト",
            description = "DBのリフレッシュトークンを失効させ、Cookie をクリアする。アクセストークンはクライアント側で破棄する。")
    @ApiResponse(responseCode = "204", description = "ログアウト成功")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        String plainToken = extractRefreshTokenFromCookie(httpRequest);
        if (plainToken != null) {
            try {
                refreshTokenService.revoke(plainToken);
            } catch (Exception e) {
                logger.warn("リフレッシュトークンの失効処理でエラーが発生しました", e);
            }
        }
        clearRefreshTokenCookie(httpResponse);
        return ResponseEntity.noContent().build();
    }

    /**
     * パスワードリセットメール送信
     */
    @Operation(summary = "パスワードリセットメール送信", description = "メールアドレスにパスワードリセット用URLを送信する。認証不要。")
    @ApiResponse(responseCode = "200", description = "処理完了（メールアドレスの存在有無に関わらず同一レスポンス）")
    @PostMapping("/password-reset/request")
    public ResponseEntity<ApiResponseDto<Void>> requestPasswordReset(
            @RequestBody PasswordResetRequestDto request) {
        if (!ValidateUtils.isEmail(request.getEmail())) {
            return ResponseEntity.ok(ApiResponseDto.success(null));
        }
        try {
            passwordResetService.requestPasswordReset(request.getEmail());
            return ResponseEntity.ok(ApiResponseDto.success(null));
        } catch (Exception e) {
            logger.warn("パスワードリセットメールの送信中にエラーが発生しました", e);
            return ResponseEntity.ok(ApiResponseDto.success(null));
        }
    }

    /**
     * パスワードリセット実行
     */
    @Operation(summary = "パスワードリセット実行", description = "リセットトークンを使用して新しいパスワードを設定する。認証不要。")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "リセット成功（data: null）"),
            @ApiResponse(responseCode = "400", description = "入力値が不正またはトークンが無効・期限切れ・使用済み",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "429", description = "試行回数が上限を超えた（15分後に解除）",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "サーバー内部エラー",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @PostMapping("/password-reset/confirm")
    public ResponseEntity<ApiResponseDto<Void>> confirmPasswordReset(
            @RequestBody PasswordResetConfirmRequestDto request,
            HttpServletRequest httpRequest) {
        String clientIp = RequestUtils.getClientIp(httpRequest);
        if (loginAttemptService.isPasswordResetBlocked(clientIp)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(ApiResponseDto.error("試行回数が上限を超えました。しばらく時間をおいて再度お試しください。"));
        }
        try {
            if (ValidateUtils.isNullOrEmpty(request.getToken())
                    || ValidateUtils.isNullOrEmpty(request.getNewPassword())
                    || ValidateUtils.isNullOrEmpty(request.getConfirmNewPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.error("すべての項目を入力してください"));
            }
            if (!ValidateUtils.isValidPassword(request.getNewPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.error(
                                "パスワードは" + ValidateUtils.PASSWORD_MIN_LENGTH + "文字以上"
                                        + ValidateUtils.PASSWORD_MAX_LENGTH + "文字以内で入力してください"));
            }
            if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.error("新しいパスワードと確認用パスワードが一致しません"));
            }
            boolean success = passwordResetService.confirmPasswordReset(
                    request.getToken(), request.getNewPassword());
            if (!success) {
                loginAttemptService.recordPasswordResetFailure(clientIp);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.error("このリンクは無効または期限切れです。再度お試しください。"));
            }
            loginAttemptService.recordPasswordResetSuccess(clientIp);
            return ResponseEntity.ok(ApiResponseDto.success(null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("パスワードのリセットに失敗しました"));
        }
    }

    /**
     * リフレッシュトークンをHttpOnly Cookieにセットする
     * <p>
     * SameSite=Lax は同一オリジン前提。
     * フロントエンドは開発時は devServer.proxy、本番は Nginx リバースプロキシで
     * 同一オリジンに統一すること（クロスオリジン XHR では Cookie が送信されない）。
     * </p>
     * @param response HTTPレスポンス
     * @param plainToken 平文リフレッシュトークン
     */
    private void setRefreshTokenCookie(HttpServletResponse response, String plainToken) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE_NAME, plainToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path(contextPath + "/auth")
                .maxAge(refreshExpirationSeconds)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * リフレッシュトークンCookieをクリアする
     * @param response HTTPレスポンス
     */
    private void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path(contextPath + "/auth")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * リクエストのCookieからリフレッシュトークンを抽出する
     * @param request HTTPリクエスト
     * @return 平文リフレッシュトークン、Cookieが存在しない場合はnull
     */
    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        return Arrays.stream(cookies)
                .filter(c -> REFRESH_COOKIE_NAME.equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}
