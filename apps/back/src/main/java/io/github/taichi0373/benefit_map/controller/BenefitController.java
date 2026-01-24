// package com.example.back.controller;

// import com.example.back.dto.BenefitSearchRequest;
// import com.example.back.service.BenefitService;
// import com.fasterxml.jackson.databind.JsonNode;
// import lombok.RequiredArgsConstructor;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import jakarta.servlet.http.HttpSession;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.Map;

// @RestController
// @RequestMapping("/benefit")
// @RequiredArgsConstructor
// @CrossOrigin
// public class BenefitController {

//     private static final Logger log = LoggerFactory.getLogger(BenefitController.class);

//     private final BenefitService benefitService;

//     @PostMapping("/search")
//     public ResponseEntity<Map<String, Object>> searchBenefits(@RequestBody BenefitSearchRequest request) {
//         try {
//             log.info("Benefit search request: {}", request);
//             Map<String, Object> result = benefitService.searchBenefits(request);
//             return ResponseEntity.ok(result);
//         } catch (Exception e) {
//             log.error("Error searching benefits", e);
//             Map<String, Object> errorResponse = new HashMap<>();
//             errorResponse.put("success", false);
//             errorResponse.put("message", "特典検索中にエラーが発生しました: " + e.getMessage());
//             errorResponse.put("benefits", new ArrayList<>());
//             return ResponseEntity.status(500).body(errorResponse);
//         }
//     }

//     @GetMapping("/users/{userId}")
//     public ResponseEntity<JsonNode> getUserBenefits(@PathVariable Long userId, HttpSession session) {
//         try {
//             log.info("=== getUserBenefits endpoint called ===");
//             log.info("Requested userId: {}", userId);
//             log.info("Session ID: {}", session.getId());
            
//             // セッション認証チェック
//             Object sessionUserId = session.getAttribute("user_id");
//             log.info("getUserBenefits - requested userId: {}, session userId: {}, session userId type: {}", 
//                     userId, sessionUserId, sessionUserId != null ? sessionUserId.getClass().getSimpleName() : "null");
            
//             if (sessionUserId == null) {
//                 log.warn("未認証のユーザーがユーザー給付金情報にアクセスしようとしました");
//                 return ResponseEntity.status(401).build();
//             }
            
//             // セッションのuser_idをLongに変換
//             Long sessionUserIdLong;
//             if (sessionUserId instanceof Integer) {
//                 sessionUserIdLong = ((Integer) sessionUserId).longValue();
//             } else if (sessionUserId instanceof Long) {
//                 sessionUserIdLong = (Long) sessionUserId;
//             } else {
//                 log.error("セッションのuser_idが予期しない型です: {}", sessionUserId.getClass());
//                 return ResponseEntity.status(500).build();
//             }
            
//             // ユーザーIDの一致確認（自分の情報のみアクセス可能）
//             if (!userId.equals(sessionUserIdLong)) {
//                 log.warn("ユーザー {} が他のユーザー {} の給付金情報にアクセスしようとしました", sessionUserIdLong, userId);
//                 return ResponseEntity.status(403).build();
//             }
            
//             log.info("Getting benefits for user: {}", userId);
//             JsonNode benefits = benefitService.getUserBenefits(userId);
//             return ResponseEntity.ok(benefits);
//         } catch (Exception e) {
//             log.error("Error getting user benefits", e);
//             return ResponseEntity.status(500).build();
//         }
//     }

//     @GetMapping("/transit")
//     public ResponseEntity<JsonNode> getTransitBenefits() {
//         try {
//             log.info("Getting transit benefits");
//             JsonNode benefits = benefitService.getTransitBenefits();
//             return ResponseEntity.ok(benefits);
//         } catch (Exception e) {
//             log.error("Error getting transit benefits", e);
//             return ResponseEntity.status(500).build();
//         }
//     }

//     @GetMapping("/stores")
//     public ResponseEntity<JsonNode> getStores() {
//         try {
//             log.info("Getting stores information");
//             JsonNode stores = benefitService.getStores();
//             return ResponseEntity.ok(stores);
//         } catch (Exception e) {
//             log.error("Error getting stores", e);
//             return ResponseEntity.status(500).build();
//         }
//     }

//     @GetMapping("/municipality")
//     public ResponseEntity<JsonNode> getMunicipalities() {
//         try {
//             log.info("Getting municipalities");
//             JsonNode municipalities = benefitService.getMunicipalities();
//             return ResponseEntity.ok(municipalities);
//         } catch (Exception e) {
//             log.error("Error getting municipalities", e);
//             return ResponseEntity.status(500).build();
//         }
//     }
// }
