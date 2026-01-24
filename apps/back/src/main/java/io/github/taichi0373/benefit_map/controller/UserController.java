package io.github.taichi0373.benefit_map.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.taichi0373.benefit_map.dto.UserProfileRequest;
import io.github.taichi0373.benefit_map.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:6006", "http://127.0.0.1:3000", "http://127.0.0.1:6006"}, allowCredentials = "true")
public class UserController {
    
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    
    private final UserService userService;
    
    /**
     * ユーザープロフィール取得
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable String userId, HttpSession session) {
        try {
            log.info("ユーザープロフィール取得: userId={}", userId);
            
            // セッション認証チェック
            Object sessionUserId = session.getAttribute("user_id");
            if (sessionUserId == null) {
                log.warn("未認証のユーザーがプロフィール情報にアクセスしようとしました");
                return ResponseEntity.status(401).build();
            }
            
            // ユーザーIDの一致確認（自分の情報のみアクセス可能）
            if (!userId.equals(sessionUserId.toString())) {
                log.warn("ユーザー {} が他のユーザー {} のプロフィール情報にアクセスしようとしました", sessionUserId, userId);
                return ResponseEntity.status(403).build();
            }
            
            Map<String, Object> result = userService.getUserProfile(userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("ユーザープロフィール取得エラー: userId={}", userId, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "プロフィール情報の取得に失敗しました");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * ユーザープロフィール更新
     */
    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> updateUserProfile(
            @PathVariable String userId,
            @RequestBody UserProfileRequest request,
            HttpSession session) {
        try {
            log.info("ユーザープロフィール更新: userId={}", userId);
            
            // セッション認証チェック
            Object sessionUserId = session.getAttribute("user_id");
            if (sessionUserId == null) {
                log.warn("未認証のユーザーがプロフィール更新を試みました");
                return ResponseEntity.status(401).build();
            }
            
            // ユーザーIDの一致確認（自分の情報のみ更新可能）
            if (!userId.equals(sessionUserId.toString())) {
                log.warn("ユーザー {} が他のユーザー {} のプロフィール更新を試みました", sessionUserId, userId);
                return ResponseEntity.status(403).build();
            }
            
            Map<String, Object> result = userService.updateUserProfile(userId, request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("ユーザープロフィール更新エラー: userId={}", userId, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "プロフィールの更新に失敗しました");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
