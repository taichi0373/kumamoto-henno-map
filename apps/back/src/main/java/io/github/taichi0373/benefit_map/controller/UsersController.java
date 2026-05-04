package io.github.taichi0373.benefit_map.controller;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import io.github.taichi0373.benefit_map.service.UsersService;
import jakarta.servlet.http.HttpSession;

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
    public ResponseEntity<ApiResponseDto<?>> getUsersInfo(@PathVariable Long userId, HttpSession session) {
        try {
            // セッション認証チェック
            Object sessionUserId = session.getAttribute("user_id");
            if (ValidateUtils.isNullOrEmpty(sessionUserId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDto.error("認証が必要です"));
            }

            // ユーザーIDの一致確認
            if (!userId.equals(sessionUserId)) {
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
            HttpSession session) {
        try {
            // セッション認証チェック
            Object sessionUserId = session.getAttribute("user_id");
            if (ValidateUtils.isNullOrEmpty(sessionUserId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDto.error("認証が必要です"));
            }

            // ユーザーIDの一致確認
            if (!Objects.equals(users.getUserId(), sessionUserId)) {
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
     * ログイン
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<?>> login(@RequestBody UsersDto users, HttpSession session) {
        try {
            UserResponseDto usersEntity = usersService.loginUser(users.getUsername(), users.getPassword());
            if (ValidateUtils.isNullOrEmpty(usersEntity)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDto.error("ユーザー名またはパスワードが正しくありません"));
            }

            session.setAttribute("user_id", usersEntity.getUserId());
            return ResponseEntity.ok(ApiResponseDto.success(usersEntity));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("ログイン中にエラーが発生しました"));
        }
    }

    /**
     * ログアウト
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto<?>> logout(HttpSession session) {
        try {
            session.invalidate();
            return ResponseEntity.ok(ApiResponseDto.success(null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("ログアウト中にエラーが発生しました"));
        }
    }

    /**
     * ユーザー登録
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto<?>> signup(@RequestBody UsersDto users, HttpSession session) {
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

            session.setAttribute("user_id", userEntity.getUserId());
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

    /**
     * セッション確認
     */
    @GetMapping("/session")
    public ResponseEntity<ApiResponseDto<?>> getSession(HttpSession session) {
        try {
            Object userId = session.getAttribute("user_id");
            if (ValidateUtils.isNullOrEmpty(userId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDto.error("セッションが存在しません"));
            }

            return ResponseEntity.ok(ApiResponseDto.success(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("セッション確認中にエラーが発生しました"));
        }
    }
}

