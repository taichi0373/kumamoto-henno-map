package io.github.taichi0373.benefit_map.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import io.github.taichi0373.benefit_map.dto.PasswordResetConfirmRequestDto;
import io.github.taichi0373.benefit_map.dto.PasswordResetRequestDto;
import io.github.taichi0373.benefit_map.service.AuthService;
import io.github.taichi0373.benefit_map.service.LoginAttemptService;
import io.github.taichi0373.benefit_map.service.PasswordResetService;
import io.github.taichi0373.benefit_map.util.ValidateUtils;
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
 * </p>
 */
@Tag(name = "認証", description = "ログイン・ログアウト・CSRFトークン取得")
@RestController
@RequestMapping("/auth")
public class AuthController {

    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    /** 認証サービス */
    @Autowired
    private AuthService authService;

    /** パスワードリセットサービス */
    @Autowired
    private PasswordResetService passwordResetService;

    /** ログイン試行管理サービス */
    @Autowired
    private LoginAttemptService loginAttemptService;

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
        String clientIp = getClientIp(httpRequest);
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
            ResponseCookie cookie = ResponseCookie.from("jwt", response.getToken())
                    .httpOnly(true)
                    .secure(cookieSecure)
                    .sameSite("Lax")
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
     * クライアントIPアドレスを取得する
     * <p>
     * リバースプロキシ経由の場合は X-Forwarded-For ヘッダーを優先する。
     * </p>
     * @param request HTTPリクエスト
     * @return クライアントIPアドレス
     */
    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
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
                .sameSite("Lax")
                .path(contextPath)
                .maxAge(0)
                .build();
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.noContent().build();
    }

    /**
     * パスワードリセットメール送信
     * <p>
     * 指定したメールアドレスにパスワードリセット用URLを送信する。
     * メールアドレスに対応するユーザーが存在しない場合も同一レスポンスを返す（列挙攻撃対策）。
     * CSRF保護は不要（/auth/**は除外済み）。
     * </p>
     * @param request パスワードリセット要求リクエスト
     * @return 処理結果（常に200 OK）
     */
    @Operation(summary = "パスワードリセットメール送信", description = "メールアドレスにパスワードリセット用URLを送信する。認証・CSRF 保護は不要。")
    @ApiResponse(responseCode = "200", description = "処理完了（メールアドレスの存在有無に関わらず同一レスポンス）")
    @PostMapping("/password-reset/request")
    public ResponseEntity<ApiResponseDto<Void>> requestPasswordReset(
            @RequestBody PasswordResetRequestDto request) {
        // メールアドレスの必須・形式チェック（無効入力はサービスを呼ばず200を返す）
        // isEmail は null/空文字でも false を返すため一括チェック可能
        if (!ValidateUtils.isEmail(request.getEmail())) {
            return ResponseEntity.ok(ApiResponseDto.success(null));
        }
        try {
            passwordResetService.requestPasswordReset(request.getEmail());
            return ResponseEntity.ok(ApiResponseDto.success(null));
        } catch (Exception e) {
            // ユーザーには成功を返す（列挙攻撃対策）が、運用検知のためWARNログを記録する
            // メールアドレス等のPIIはログに含めない
            logger.warn("パスワードリセットメールの送信中にエラーが発生しました", e);
            return ResponseEntity.ok(ApiResponseDto.success(null));
        }
    }

    /**
     * パスワードリセット実行
     * <p>
     * リセットトークンを使用して新しいパスワードを設定する。
     * CSRF保護は不要（/auth/**は除外済み）。
     * </p>
     * @param request パスワードリセット実行リクエスト
     * @return 処理結果
     */
    @Operation(summary = "パスワードリセット実行", description = "リセットトークンを使用して新しいパスワードを設定する。認証・CSRF 保護は不要。")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "リセット成功（data: null）"),
            @ApiResponse(responseCode = "400", description = "入力値が不正（必須項目未入力・パスワード不一致・長さ不足）またはトークンが無効・期限切れ・使用済み",
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
        String clientIp = getClientIp(httpRequest);
        if (loginAttemptService.isPasswordResetBlocked(clientIp)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(ApiResponseDto.error("試行回数が上限を超えました。しばらく時間をおいて再度お試しください。"));
        }
        try {
            // 必須チェック
            if (ValidateUtils.isNullOrEmpty(request.getToken())
                    || ValidateUtils.isNullOrEmpty(request.getNewPassword())
                    || ValidateUtils.isNullOrEmpty(request.getConfirmNewPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.error("すべての項目を入力してください"));
            }

            // パスワードポリシーチェック（最低文字数）
            if (!ValidateUtils.isValidPassword(request.getNewPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.error(
                                "パスワードは" + ValidateUtils.PASSWORD_MIN_LENGTH + "文字以上で入力してください"));
            }

            // 新パスワード一致チェック
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
