package io.github.taichi0373.benefit_map.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<Map<String, Object>> getUsersInfo(@PathVariable Integer userId, HttpSession session) {
        try {
            // セッション認証チェック
            Object sessionUserId = session.getAttribute("user_id");
            if (ValidateUtils.isNullOrEmpty(sessionUserId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // ユーザーIDの一致確認（自分の情報のみ取得可能）
            if (!Objects.equals(userId, sessionUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // ユーザー情報取得
            UsersEntity user = usersService.getUsersInfo(userId);
            if (ValidateUtils.isNullOrEmpty(user)) {
                return ResponseEntity.noContent().build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("data", user);
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
            
            // ユーザーIDの一致確認（自分の情報のみ更新可能）
            if (!Objects.equals(users.getUserId(), sessionUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // ユーザー情報更新
            Integer result = usersService.updateUsersInfo(users);
            if (ValidateUtils.isNullOrEmpty(result)) {
                return ResponseEntity.noContent().build();
            }
            Map<String, Object> response = new HashMap<>();
            response.put("message", "ユーザー情報が更新されました");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
