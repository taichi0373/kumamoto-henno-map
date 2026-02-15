package io.github.taichi0373.benefit_map.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import io.github.taichi0373.benefit_map.dto.BenefitEligibilityDto;
import io.github.taichi0373.benefit_map.service.BenefitService;
import io.github.taichi0373.benefit_map.util.ValidateUtils;
import io.github.taichi0373.benefit_map.repository.entity.BenefitEntity;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/benefit")
public class BenefitController {
    
    /**
     * 特典情報サービス
     */
    @Autowired
    private BenefitService benefitService;
    
    /**
     * 特典検索
     */
    @PostMapping("/search")
    public ResponseEntity<Map<String, Object>> searchBenefits(@RequestBody BenefitEligibilityDto request) {
        try {
            List<BenefitEntity> benefit = benefitService.searchBenefits(request);
            Map<String, Object> data = new HashMap<>();
            data.put("success", true);
            data.put("message", "特典検索に成功しました");
            data.put("benefits", benefit);
            Map<String, Object> response = new HashMap<>();
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("success", false);
            errorData.put("message", "特典検索中にエラーが発生しました: " + e.getMessage());
            errorData.put("benefits", new ArrayList<>());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("data", errorData);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * ユーザー特典取得
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> getUsersBenefits(@PathVariable Long userId, HttpSession session) {
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
            
            List<BenefitEntity> benefit = benefitService.getUsersBenefits(userId);
            Map<String, Object> data = new HashMap<>();
            data.put("success", true);
            data.put("message", "ユーザー特典情報を取得しました");
            data.put("benefits", benefit);
            Map<String, Object> response = new HashMap<>();
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("success", false);
            errorData.put("message", "ユーザー特典情報の取得に失敗しました: " + e.getMessage());
            errorData.put("benefits", new ArrayList<>());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("data", errorData);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
