package io.github.taichi0373.benefit_map.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.taichi0373.benefit_map.dto.BenefitSearchRequest;
import io.github.taichi0373.benefit_map.service.BenefitService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/benefit")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:6006", "http://127.0.0.1:3000", "http://127.0.0.1:6006"}, allowCredentials = "true")
public class BenefitController {
    
    private static final Logger log = LoggerFactory.getLogger(BenefitController.class);
    
    private final BenefitService benefitService;
    
    /**
     * 特典検索
     */
    @PostMapping("/search")
    public ResponseEntity<Map<String, Object>> searchBenefits(@RequestBody BenefitSearchRequest request) {
        try {
            log.info("特典検索リクエスト: {}", request);
            Map<String, Object> result = benefitService.searchBenefits(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("特典検索エラー", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "特典検索中にエラーが発生しました: " + e.getMessage());
            errorResponse.put("benefits", new ArrayList<>());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * ユーザー特典取得
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> getUserBenefits(@PathVariable String userId, HttpSession session) {
        try {
            log.info("ユーザー特典取得: userId={}", userId);
            
            // セッション認証チェック
            Object sessionUserId = session.getAttribute("user_id");
            if (sessionUserId == null) {
                log.warn("未認証のユーザーがユーザー特典情報にアクセスしようとしました");
                return ResponseEntity.status(401).build();
            }
            
            // ユーザーIDの一致確認（自分の情報のみアクセス可能）
            if (!userId.equals(sessionUserId.toString())) {
                log.warn("ユーザー {} が他のユーザー {} の特典情報にアクセスしようとしました", sessionUserId, userId);
                return ResponseEntity.status(403).build();
            }
            
            Map<String, Object> result = benefitService.getUserBenefits(userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("ユーザー特典取得エラー: userId={}", userId, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "ユーザー特典情報の取得に失敗しました");
            errorResponse.put("benefits", new ArrayList<>());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
