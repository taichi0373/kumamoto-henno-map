package io.github.taichi0373.benefit_map.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import io.github.taichi0373.benefit_map.service.AuthService;
import io.github.taichi0373.benefit_map.service.LoginAttemptService;
import io.github.taichi0373.benefit_map.service.PasswordResetService;
import io.github.taichi0373.benefit_map.util.RequestUtils;
import io.github.taichi0373.benefit_map.util.ValidateUtils;
import jakarta.servlet.http.HttpServletRequest;
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
 * ログイン成功時はJWTトークンをレスポンスボディで返す。
 * クライアントは以降のリクエストで Authorization: Bearer <token> ヘッダーを付与する。
 * </p>
 */
@Tag(name = "認証", description = "ログイン・ログアウト")
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

    /**
     * ログイン
     * <p>
     * 認証成功時にJWTトークンをレスポンスボディで返す。
     * クライアントはトークンを保存し、以降のAPIリクエストに
     * Authorization: Bearer ヘッダーとして付与すること。
     * </p>
     * @param request ログインリクエスト
     * @param httpRequest HTTPリクエスト
     * @return JWTトークンとユーザー情報
     */
    @Operation(summary = "ログイン", description = "ユーザー名とパスワードで認証し、JWTトークンをレスポンスボディで返す。以降のAPIリクエストには Authorization: Bearer <token> ヘッダーを付与すること。")
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
            HttpServletRequest httpRequest) {
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
            return ResponseEntity.ok(ApiResponseDto.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("ログイン中にエラーが発生しました"));
        }
    }

    /**
     * ログアウト
     * <p>
     * クライアント側でトークンを破棄することでログアウトが完了する。
     * サーバー側はSTATELESSのため、トークンの無効化は行わない。
     * </p>
     * @return 204 No Content
     */
    @Operation(summary = "ログアウト", description = "クライアント側のトークンを破棄する。サーバー側の処理なし（STATELESSのため）。")
    @ApiResponse(responseCode = "204", description = "ログアウト成功（ボディなし）")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }

    /**
     * パスワードリセットメール送信
     * <p>
     * 指定したメールアドレスにパスワードリセット用URLを送信する。
     * メールアドレスに対応するユーザーが存在しない場合も同一レスポンスを返す（列挙攻撃対策）。
     * </p>
     * @param request パスワードリセット要求リクエスト
     * @return 処理結果（常に200 OK）
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
     * <p>
     * リセットトークンを使用して新しいパスワードを設定する。
     * </p>
     * @param request パスワードリセット実行リクエスト
     * @param httpRequest HTTPリクエスト
     * @return 処理結果
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
                                "パスワードは" + ValidateUtils.PASSWORD_MIN_LENGTH + "文字以上で入力してください"));
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
}
