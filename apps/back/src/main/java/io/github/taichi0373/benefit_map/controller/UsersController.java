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
import io.github.taichi0373.benefit_map.dto.UserResponseDto;
import io.github.taichi0373.benefit_map.dto.UsersDto;
import io.github.taichi0373.benefit_map.exception.DuplicateUserException;
import io.github.taichi0373.benefit_map.security.CustomUserDetails;
import io.github.taichi0373.benefit_map.service.UsersService;

/**
 * ユーザーコントローラー
 * <p>
 * ユーザー情報の取得・更新・登録を行うエンドポイントを提供する。
 * 認証が必要なエンドポイントは JWT トークンで認証する。
 * </p>
 */
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
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponseDto<?>> getUsersInfo(@PathVariable Long userId, Authentication auth) {
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
    @PutMapping
    public ResponseEntity<ApiResponseDto<?>> updateUserProfile(
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
     * ユーザー登録
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto<?>> signup(@RequestBody UsersDto users) {
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
