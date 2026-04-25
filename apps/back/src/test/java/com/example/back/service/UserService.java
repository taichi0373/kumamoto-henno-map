package com.example.back.service;

import com.example.back.dto.RegisterRequest;
import com.example.back.dto.UserProfileRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Map<String, Object> authenticateUser(String username, String password) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT id, username, password, birth_date, address, license_status FROM users WHERE username = ?";
            Map<String, Object> user = jdbcTemplate.queryForMap(sql, username);
            
            String storedPasswordHash = (String) user.get("password");
            
            if (passwordEncoder.matches(password, storedPasswordHash)) {
                user.remove("password"); // パスワードハッシュは返さない
                result.put("success", true);
                result.put("user", user);
            } else {
                result.put("success", false);
                result.put("message", "ユーザー名またはパスワードが正しくありません");
            }
            
        } catch (Exception e) {
            log.error("Authentication error for username: {}", username, e);
            result.put("success", false);
            result.put("message", "ユーザー名またはパスワードが正しくありません");
        }
        return result;
    }

    public Map<String, Object> registerUser(RegisterRequest userData) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String username = userData.getUsername();
            
            // ユーザー名の重複チェック
            String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, username);
            
            if (count > 0) {
                result.put("success", false);
                result.put("message", "このユーザー名は既に登録されています");
                return result;
            }
            
            // パスワードをハッシュ化
            String password = userData.getPassword();
            String passwordHash = passwordEncoder.encode(password);
            
            // ユーザーを登録（idはSERIAL PRIMARY KEYで自動生成）
            String insertSql = """
                INSERT INTO users (username, password, birth_date, address, license_status) 
                VALUES (?, ?, ?, ?, ?)
                """;
            
            jdbcTemplate.update(insertSql,
                userData.getUsername(),
                passwordHash,
                userData.getBirthDate(),
                userData.getAddress(),
                userData.getLicenseStatus()
            );
            
            result.put("success", true);
            result.put("message", "ユーザー登録が完了しました");
            
        } catch (Exception e) {
            log.error("Registration error", e);
            result.put("success", false);
            result.put("message", "ユーザー登録に失敗しました");
        }
        
        return result;
    }

    public Map<String, Object> getUserProfile(Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            log.info("Getting user profile for userId: {}", userId);
            String sql = "SELECT id, username, birth_date, address, license_status FROM users WHERE id = ?";
            log.info("Executing SQL: {} with userId: {}", sql, userId);
            
            Map<String, Object> user = jdbcTemplate.queryForMap(sql, userId);
            log.info("Retrieved user data: {}", user);
            
            result.put("success", true);
            result.put("profile", user);
            log.info("Successfully retrieved profile for user: {}", userId);
            
        } catch (Exception e) {
            log.error("Error getting user profile for user: {}", userId, e);
            result.put("success", false);
            result.put("message", "プロフィール情報の取得に失敗しました");
        }
        
        return result;
    }
    
    // デバッグ用：すべてのユーザを取得（本番環境では削除）
    public Map<String, Object> getAllUsers() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT id, username, birth_date, address, license_status FROM users";
            List<Map<String, Object>> users = jdbcTemplate.queryForList(sql);
            log.info("Found {} users in database", users.size());
            
            result.put("success", true);
            result.put("users", users);
            result.put("count", users.size());
            
        } catch (Exception e) {
            log.error("Error getting all users", e);
            result.put("success", false);
            result.put("message", "ユーザ一覧の取得に失敗しました");
        }
        
        return result;
    }

    public Map<String, Object> updateUserProfile(Long userId, UserProfileRequest userData) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = """
                UPDATE users 
                SET username = ?, birth_date = ?, address = ?, license_status = ? 
                WHERE id = ?
                """;
            
            jdbcTemplate.update(sql,
                userData.getUsername(),
                userData.getBirthDate(),
                userData.getAddress(),
                userData.getLicenseStatus(),
                userId
            );
            
            // 更新されたユーザー情報を取得
            Map<String, Object> profileResult = getUserProfile(userId);
            
            result.put("success", true);
            result.put("message", "プロフィールが更新されました");
            result.put("user", ((Map<String, Object>) profileResult.get("profile")));
            
        } catch (Exception e) {
            log.error("Error updating user profile for user: {}", userId, e);
            result.put("success", false);
            result.put("message", "プロフィールの更新に失敗しました");
        }
        
        return result;
    }
}
