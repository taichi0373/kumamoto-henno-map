// package com.example.back.controller;

// import com.example.back.dto.UserProfileRequest;
// import com.example.back.service.UserService;
// import lombok.RequiredArgsConstructor;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import jakarta.servlet.http.HttpSession;
// import java.util.Map;

// @RestController
// @RequestMapping("/user")
// @RequiredArgsConstructor
// @CrossOrigin
// public class UserController {

//     private static final Logger log = LoggerFactory.getLogger(UserController.class);

//     private final UserService userService;

//     @GetMapping("/profile")
//     public ResponseEntity<Map<String, Object>> getProfile(HttpSession session) {
//         try {
//             log.info("Profile request received. Session ID: {}", session.getId());
//             Object userId = session.getAttribute("user_id");
//             Object username = session.getAttribute("username");
            
//             log.info("Session attributes - user_id: {}, username: {}, user_id type: {}", 
//                     userId, username, userId != null ? userId.getClass().getSimpleName() : "null");
            
//             if (userId == null) {
//                 log.warn("No user_id in session. Returning 401.");
//                 return ResponseEntity.status(401).body(Map.of("success", false, "message", "認証が必要です"));
//             }

//             // セッションのuser_idをLongに変換
//             Long userIdLong;
//             if (userId instanceof Integer) {
//                 userIdLong = ((Integer) userId).longValue();
//                 log.info("Converted Integer user_id {} to Long {}", userId, userIdLong);
//             } else if (userId instanceof Long) {
//                 userIdLong = (Long) userId;
//                 log.info("Using Long user_id {}", userIdLong);
//             } else {
//                 log.error("セッションのuser_idが予期しない型です: {}", userId.getClass());
//                 return ResponseEntity.status(500).body(Map.of("success", false, "message", "サーバーエラーが発生しました"));
//             }

//             log.info("Calling getUserProfile for userId: {}", userIdLong);
//             Map<String, Object> result = userService.getUserProfile(userIdLong);
//             log.info("UserService returned: {}", result);
            
//             return ResponseEntity.ok(result);
//         } catch (Exception e) {
//             log.error("Error getting user profile", e);
//             return ResponseEntity.status(500).body(Map.of("success", false, "message", "サーバーエラーが発生しました"));
//         }
//     }
    
//     // デバッグ用：すべてのユーザを取得（本番環境では削除）
//     @GetMapping("/debug/users")
//     public ResponseEntity<Map<String, Object>> getAllUsers() {
//         try {
//             log.info("Debug: Getting all users");
//             Map<String, Object> result = userService.getAllUsers();
//             return ResponseEntity.ok(result);
//         } catch (Exception e) {
//             log.error("Error getting all users", e);
//             return ResponseEntity.status(500).body(Map.of("success", false, "message", "サーバーエラーが発生しました"));
//         }
//     }

//     @PostMapping("/profile")
//     public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody UserProfileRequest request, HttpSession session) {
//         try {
//             Object userId = session.getAttribute("user_id");
//             log.info("Update profile - user_id: {}, type: {}", userId, 
//                     userId != null ? userId.getClass().getSimpleName() : "null");
            
//             if (userId == null) {
//                 return ResponseEntity.status(401).body(Map.of("success", false, "message", "認証が必要です"));
//             }

//             // セッションのuser_idをLongに変換
//             Long userIdLong;
//             if (userId instanceof Integer) {
//                 userIdLong = ((Integer) userId).longValue();
//                 log.info("Converted Integer user_id {} to Long {}", userId, userIdLong);
//             } else if (userId instanceof Long) {
//                 userIdLong = (Long) userId;
//                 log.info("Using Long user_id {}", userIdLong);
//             } else {
//                 log.error("セッションのuser_idが予期しない型です: {}", userId.getClass());
//                 return ResponseEntity.status(500).body(Map.of("success", false, "message", "サーバーエラーが発生しました"));
//             }

//             Map<String, Object> result = userService.updateUserProfile(userIdLong, request);
            
//             if ((Boolean) result.get("success")) {
//                 Map<String, Object> user = (Map<String, Object>) result.get("user");
//                 session.setAttribute("username", user.get("username"));
//             }
            
//             return ResponseEntity.ok(result);
//         } catch (Exception e) {
//             log.error("Error updating user profile", e);
//             return ResponseEntity.status(500).build();
//         }
//     }
// }
