package io.github.taichi0373.benefit_map.controller;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.taichi0373.benefit_map.util.ValidateUtils;
import io.github.taichi0373.benefit_map.dto.ApiResponseDto;
import io.github.taichi0373.benefit_map.dto.ChangePasswordRequestDto;
import io.github.taichi0373.benefit_map.dto.UserResponseDto;
import io.github.taichi0373.benefit_map.dto.UsersDto;
import io.github.taichi0373.benefit_map.exception.DuplicateUserException;
import io.github.taichi0373.benefit_map.security.CustomUserDetails;
import io.github.taichi0373.benefit_map.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * ユーザーコントローラー
 * <p>
 * ユーザー情報の取得・更新・登録を行うエンドポイントを提供する。
 * 認証が必要なエンドポイントは JWT トークンで認証する。
 * </p>
 */
@Tag(name = "ユーザー", description = "ユーザー情報の取得・更新・登録")
@RestController
@RequestMapping("/users")
public class UsersController {

    /**
     * ユーザ情報取得サービス
     */
    @Autowired
    private UsersService usersService;

    /**
     * ユーザー情報取得
     */
    @Operation(summary = "ユーザー情報取得", description = "JWT で認証されたユーザー自身の情報を取得する。")
    @SecurityRequirement(name = "cookieAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "取得成功（data: UserResponseDto）"),
            @ApiResponse(responseCode = "401", description = "未認証",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "他ユーザーへのアクセス",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "ユーザーが存在しない",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> getUsersInfo(@PathVariable Long userId, Authentication auth) {
        try {
            // JWT認証チェック
            if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof CustomUserDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDto.error("認証が必要です"));
            }

            // ユーザーIDの一致確認
            CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
            if (!userId.equals(principal.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponseDto.error("アクセス権限がありません"));
            }

            // ユーザー情報取得
            UserResponseDto user = usersService.getUsersInfo(userId);
            if (ValidateUtils.isNullOrEmpty(user)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.error("ユーザーが見つかりません"));
            }

            return ResponseEntity.ok(ApiResponseDto.success(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("ユーザー情報の取得に失敗しました"));
        }
    }

    /**
     * ユーザー情報の更新
     */
    @Operation(summary = "ユーザー情報更新", description = "JWT で認証されたユーザー自身の情報を更新する。CSRF トークン必須。")
    @SecurityRequirements({
            @SecurityRequirement(name = "cookieAuth"),
            @SecurityRequirement(name = "csrfToken")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功（data: null）"),
            @ApiResponse(responseCode = "401", description = "未認証",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "他ユーザーへのアクセス",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "ユーザーが存在しない",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @PutMapping
    public ResponseEntity<ApiResponseDto<Void>> updateUserProfile(
            @RequestBody UsersDto users,
            Authentication auth) {
        try {
            // JWT認証チェック
            if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof CustomUserDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDto.error("認証が必要です"));
            }

            // ユーザーIDの一致確認
            CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
            if (!Objects.equals(users.getUserId(), principal.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponseDto.error("アクセス権限がありません"));
            }

            // ユーザー情報更新
            Integer result = usersService.updateUsersInfo(users);
            if (ValidateUtils.isNullOrEmpty(result)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.error("ユーザーが見つかりません"));
            }

            return ResponseEntity.ok(ApiResponseDto.success(null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("ユーザー情報の更新に失敗しました"));
        }
    }

    /**
     * パスワード変更
     * <p>
     * ログイン済みユーザーが現在のパスワードを確認したうえで新しいパスワードに変更する。
     * </p>
     */
    @Operation(summary = "パスワード変更", description = "JWT で認証されたユーザー自身のパスワードを変更する。現在のパスワードの確認が必要。CSRF トークン必須。")
    @SecurityRequirements({
            @SecurityRequirement(name = "cookieAuth"),
            @SecurityRequirement(name = "csrfToken")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "変更成功（data: null）"),
            @ApiResponse(responseCode = "400", description = "入力値が不正（必須項目未入力・パスワード不一致）",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "未認証",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "ユーザーが存在しない",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "現在のパスワードが正しくない",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @PutMapping("/password")
    public ResponseEntity<ApiResponseDto<Void>> changePassword(
            @RequestBody ChangePasswordRequestDto request,
            Authentication auth) {
        try {
            // JWT認証チェック
            if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof CustomUserDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDto.error("認証が必要です"));
            }

            // 必須チェック
            if (ValidateUtils.isNullOrEmpty(request.getCurrentPassword())
                    || ValidateUtils.isNullOrEmpty(request.getNewPassword())
                    || ValidateUtils.isNullOrEmpty(request.getConfirmNewPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.error("すべての項目を入力してください"));
            }

            // 新パスワード一致チェック
            if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.error("新しいパスワードと確認用パスワードが一致しません"));
            }

            CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
            Boolean result = usersService.changePassword(
                    principal.getUserId(),
                    request.getCurrentPassword(),
                    request.getNewPassword());

            if (result == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.error("ユーザーが見つかりません"));
            }
            if (Boolean.FALSE.equals(result)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponseDto.error("現在のパスワードが正しくありません"));
            }

            return ResponseEntity.ok(ApiResponseDto.success(null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("パスワードの変更に失敗しました"));
        }
    }

    /**
     * ユーザー登録
     */
    @Operation(summary = "ユーザー登録", description = "新規アカウントを作成する。認証・CSRF 保護は不要。")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "登録成功（data: UserResponseDto）"),
            @ApiResponse(responseCode = "409", description = "ユーザー名が既に使用されている",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "503", description = "DB接続エラー",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> signup(@RequestBody UsersDto users) {
        try {
            // ユーザー名の重複チェック
            Boolean userExists = usersService.existsByUsername(users.getUsername());
            if (Boolean.TRUE.equals(userExists)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponseDto.error("このユーザー名は既に使用されています"));
            }

            // ユーザー登録処理
            UserResponseDto userEntity = usersService.signupUser(users);
            if (ValidateUtils.isNullOrEmpty(userEntity)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponseDto.error("ユーザー登録に失敗しました"));
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDto.success(userEntity));
        } catch (DuplicateUserException e) {
            // データベース制約違反による重複エラー
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (RuntimeException e) {
            // ユーザー名重複チェック時のデータベースエラー等
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponseDto.error("データベース接続エラーが発生しました。しばらく時間をおいて再度お試しください。"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("サーバーエラーが発生しました"));
        }
    }
}
