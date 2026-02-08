package io.github.taichi0373.benefit_map.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.taichi0373.benefit_map.dto.UsersDto;
import io.github.taichi0373.benefit_map.repository.entity.UsersEntity;
import io.github.taichi0373.benefit_map.service.UsersService;
import io.github.taichi0373.benefit_map.util.ValidateUtils;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/auth")
public class AuthController {

    /**
     * ユーザー情報サービス
     */
    @Autowired
    private UsersService usersService;

    /**
     * ログイン
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UsersDto users) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            UsersEntity usersEntity = usersService.loginUser(users.getUsername(), users.getPassword());
            
            if (ValidateUtils.isNullOrEmpty(usersEntity)) {
                return ResponseEntity.noContent().build();
            } else {
                result.put("usersEntity", usersEntity);
                result.put("message", "");
                return ResponseEntity.ok(result);
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
            return ResponseEntity.ok(Map.of("message", "ログアウトしました"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * ユーザー登録
     */
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody UsersDto users) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            System.out.println("signup users: " + users);
            UsersEntity userEntity = usersService.signupUser(users);
            if (ValidateUtils.isNullOrEmpty(userEntity)) {
                return ResponseEntity.noContent().build();
            } else {
                result.put("usersEntity", userEntity);
                result.put("message", "ユーザー登録が完了しました");
                return ResponseEntity.ok(result);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * セッション確認
     */
    @GetMapping("/session")
    public ResponseEntity<Map<String, Object>> getSession(HttpSession session) {
        try {
            Object userId = session.getAttribute("userId");
            if (ValidateUtils.isNullOrEmpty(userId)) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.ok(Map.of("userId", userId));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
