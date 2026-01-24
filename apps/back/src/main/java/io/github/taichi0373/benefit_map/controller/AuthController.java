package io.github.taichi0373.benefit_map.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.taichi0373.benefit_map.dto.LoginRequest;
import io.github.taichi0373.benefit_map.dto.RegisterRequest;
import io.github.taichi0373.benefit_map.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:6006", "http://127.0.0.1:3000", "http://127.0.0.1:6006"}, allowCredentials = "true")
public class AuthController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    private final UserService userService;
    
    /**
     * ログイン
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request, HttpSession session) {
        try {
            log.info("ログイン試行: username={}", request.getUsername());
            Map<String, Object> result = userService.authenticateUser(request.getUsername(), request.getPassword());
            
            if ((Boolean) result.get("success")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> user = (Map<String, Object>) result.get("user");
                session.setAttribute("user_id", user.get("userId"));
                session.setAttribute("username", user.get("username"));
                log.info("ユーザー {} がログインしました", user.get("username"));
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("ログインエラー", e);
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * ログアウト
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        try {
            session.invalidate();
            log.info("ユーザーがログアウトしました");
            return ResponseEntity.ok(Map.of("success", true, "message", "ログアウトしました"));
        } catch (Exception e) {
            log.error("ログアウトエラー", e);
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * ユーザー登録
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        try {
            log.info("ユーザー登録試行: username={}", request.getUsername());
            Map<String, Object> result = userService.registerUser(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("ユーザー登録エラー", e);
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * セッション確認
     */
    @GetMapping("/session")
    public ResponseEntity<Map<String, Object>> getSession(HttpSession session) {
        try {
            Object userId = session.getAttribute("user_id");
            Object username = session.getAttribute("username");
            
            if (userId != null) {
                return ResponseEntity.ok(Map.of(
                    "authenticated", true,
                    "user_id", userId,
                    "username", username
                ));
            } else {
                return ResponseEntity.ok(Map.of("authenticated", false));
            }
        } catch (Exception e) {
            log.error("セッション確認エラー", e);
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * ヘルスチェック
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "message", "Backend server is running",
            "timestamp", System.currentTimeMillis()
        ));
    }
}
