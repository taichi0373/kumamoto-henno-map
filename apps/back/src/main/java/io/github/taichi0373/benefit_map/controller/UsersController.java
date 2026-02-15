package io.github.taichi0373.benefit_map.controller;

import java.util.HashMap;
import java.util.Map;
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
import io.github.taichi0373.benefit_map.dto.UsersDto;
import io.github.taichi0373.benefit_map.repository.entity.UsersEntity;
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
    public ResponseEntity<Map<String, Object>> getUsersInfo(@PathVariable Long userId, HttpSession session) {
        try {
            // セッション認証チェック
            Object sessionUserId = session.getAttribute("user_id");
            if (ValidateUtils.isNullOrEmpty(sessionUserId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // ユーザーIDの一致確認
            if (!userId.equals(sessionUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // ユーザー情報取得
            UsersEntity user = usersService.getUsersInfo(userId);
            if (ValidateUtils.isNullOrEmpty(user)) {
                return ResponseEntity.noContent().build();
            }

            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            Map<String, Object> response = new HashMap<>();
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
    
    /**
     * ユーザー情報の更新
     */
    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> updateUserProfile(
            @RequestBody UsersDto users,
            HttpSession session) {
        try {
            
            // セッション認証チェック
            Object sessionUserId = session.getAttribute("user_id");
            if (ValidateUtils.isNullOrEmpty(sessionUserId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // ユーザーIDの一致確認
            if (!Objects.equals(users.getUserId(), sessionUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // ユーザー情報更新
            Integer result = usersService.updateUsersInfo(users);
            if (ValidateUtils.isNullOrEmpty(result)) {
                return ResponseEntity.noContent().build();
            }
            Map<String, Object> data = new HashMap<>();
            data.put("message", "ユーザー情報が更新されました");
            Map<String, Object> response = new HashMap<>();
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * ログイン
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UsersDto users, HttpSession session) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            UsersEntity usersEntity = usersService.loginUser(users.getUsername(), users.getPassword());
            if (ValidateUtils.isNullOrEmpty(usersEntity)) {
                Map<String, Object> errorData = new HashMap<>();
                errorData.put("error", "ユーザー名またはパスワードが正しくありません");
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("data", errorData);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            } else {
                session.setAttribute("user_id", usersEntity.getUserId());
                Map<String, Object> data = new HashMap<>();
                data.put("usersEntity", usersEntity);
                data.put("message", "");
                Map<String, Object> response = new HashMap<>();
                response.put("data", data);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * ログアウト
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        try {
            session.invalidate();
            Map<String, Object> data = new HashMap<>();
            data.put("message", "ログアウトしました");
            Map<String, Object> response = new HashMap<>();
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * ユーザー登録
     */
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody UsersDto users, HttpSession session) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            // ユーザー名の重複チェック
            UsersEntity existingUser = usersService.getUserByUsername(users.getUsername());
            if (!ValidateUtils.isNullOrEmpty(existingUser)) {
                Map<String, Object> errorData = new HashMap<>();
                errorData.put("error", "このユーザー名は既に使用されています");
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("data", errorData);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            // ユーザー登録処理
            UsersEntity userEntity = usersService.signupUser(users);
            if (ValidateUtils.isNullOrEmpty(userEntity)) {
                Map<String, Object> errorData = new HashMap<>();
                errorData.put("error", "ユーザー登録に失敗しました");
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("data", errorData);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            } else {
                session.setAttribute("user_id", userEntity.getUserId());
                Map<String, Object> data = new HashMap<>();
                data.put("usersEntity", userEntity);
                data.put("message", "ユーザー登録が完了しました");
                Map<String, Object> response = new HashMap<>();
                response.put("data", data);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("error", "サーバーエラーが発生しました");
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("data", errorData);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * セッション確認
     */
    @GetMapping("/session")
    public ResponseEntity<Map<String, Object>> getSession(HttpSession session) {
        try {
            Object userId = session.getAttribute("user_id");
            if (ValidateUtils.isNullOrEmpty(userId)) {
                return ResponseEntity.noContent().build();
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("userId", userId);
                Map<String, Object> response = new HashMap<>();
                response.put("data", data);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}

