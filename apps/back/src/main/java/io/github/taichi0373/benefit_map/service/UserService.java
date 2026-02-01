package io.github.taichi0373.benefit_map.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.taichi0373.benefit_map.dto.RegisterRequest;
import io.github.taichi0373.benefit_map.dto.UserProfileRequest;
import io.github.taichi0373.benefit_map.repository.dao.UsersDao;
import io.github.taichi0373.benefit_map.repository.entity.SystemField;
import io.github.taichi0373.benefit_map.repository.entity.UsersEntity;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    private final UsersDao usersDao;
    private final BCryptPasswordEncoder passwordEncoder;
    
    /**
     * ユーザー認証
     */
    public Map<String, Object> authenticateUser(String username, String password) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            UsersEntity user = usersDao.selectByUsername(username);
            
            if (user == null) {
                result.put("success", false);
                result.put("message", "ユーザー名またはパスワードが正しくありません");
                return result;
            }
            
            if (passwordEncoder.matches(password, user.getPasswordHash())) {
                Map<String, Object> userMap = convertToMap(user);
                userMap.remove("passwordHash"); // パスワードハッシュは返さない
                
                result.put("success", true);
                result.put("user", userMap);
            } else {
                result.put("success", false);
                result.put("message", "ユーザー名またはパスワードが正しくありません");
            }
            
        } catch (Exception e) {
            log.error("認証エラー: username={}", username, e);
            result.put("success", false);
            result.put("message", "ユーザー名またはパスワードが正しくありません");
        }
        
        return result;
    }
    
    /**
     * ユーザー登録
     */
    public Map<String, Object> registerUser(RegisterRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // ユーザー名の重複チェック
            UsersEntity existingUser = usersDao.selectByUsername(request.getUsername());
            if (existingUser != null) {
                result.put("success", false);
                result.put("message", "このユーザー名は既に登録されています");
                return result;
            }
            
            // パスワードをハッシュ化
            String passwordHash = passwordEncoder.encode(request.getPassword());
            
            // ユーザーエンティティを作成
            UsersEntity user = new UsersEntity();
            user.setUserId(UUID.randomUUID().toString().substring(0, 10));
            user.setUsername(request.getUsername());
            user.setPasswordHash(passwordHash);
            user.setEmail(null); // 必要に応じて設定
            user.setBirthDate(request.getBirthDate());
            user.setMunicipalityCode(request.getAddress());
            user.setLicenseStatus(request.getLicenseStatus());
            user.setLicenseSurrenderedAt(null);
            
            // システム共通フィールドを設定
            LocalDateTime now = LocalDateTime.now();
            SystemField systemField = new SystemField(now, now);
            user.setSystemField(systemField);
            
            // ユーザーを登録
            int inserted = usersDao.insert(user);
            
            if (inserted > 0) {
                result.put("success", true);
                result.put("message", "ユーザー登録が完了しました");
            } else {
                result.put("success", false);
                result.put("message", "ユーザー登録に失敗しました");
            }
            
        } catch (Exception e) {
            log.error("ユーザー登録エラー", e);
            result.put("success", false);
            result.put("message", "ユーザー登録に失敗しました");
        }
        
        return result;
    }
    
    /**
     * ユーザープロフィール取得
     */
    public Map<String, Object> getUserProfile(String userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            log.info("ユーザープロフィール取得: userId={}", userId);
            
            UsersEntity user = usersDao.selectById(userId);
            if (user == null) {
                result.put("success", false);
                result.put("message", "ユーザーが見つかりませんでした");
                return result;
            }
            
            Map<String, Object> userMap = convertToMap(user);
            userMap.remove("passwordHash"); // パスワードハッシュは返さない
            
            result.put("success", true);
            result.put("profile", userMap);
            
        } catch (Exception e) {
            log.error("ユーザープロフィール取得エラー: userId={}", userId, e);
            result.put("success", false);
            result.put("message", "プロフィール情報の取得に失敗しました");
        }
        
        return result;
    }
    
    /**
     * ユーザープロフィール更新
     */
    public Map<String, Object> updateUserProfile(String userId, UserProfileRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            UsersEntity user = usersDao.selectById(userId);
            if (user == null) {
                result.put("success", false);
                result.put("message", "ユーザーが見つかりませんでした");
                return result;
            }
            
            // プロフィール情報を更新
            user.setUsername(request.getUsername());
            user.setBirthDate(request.getBirthDate());
            user.setMunicipalityCode(request.getAddress());
            user.setLicenseStatus(request.getLicenseStatus());
            
            // システム共通フィールドの更新日時を更新
            if (user.getSystemField() != null) {
                SystemField systemField = new SystemField(
                    user.getSystemField().getSysCreatedAt(),
                    LocalDateTime.now()
                );
                user.setSystemField(systemField);
            }
            
            int updated = usersDao.update(user);
            
            if (updated > 0) {
                // 更新されたユーザー情報を取得
                Map<String, Object> profileResult = getUserProfile(userId);
                result.put("success", true);
                result.put("message", "プロフィールが更新されました");
                result.put("user", ((Map<String, Object>) profileResult.get("profile")));
            } else {
                result.put("success", false);
                result.put("message", "プロフィールの更新に失敗しました");
            }
            
        } catch (Exception e) {
            log.error("ユーザープロフィール更新エラー: userId={}", userId, e);
            result.put("success", false);
            result.put("message", "プロフィールの更新に失敗しました");
        }
        
        return result;
    }
    
    /**
     * UsersEntityをMapに変換
     */
    private Map<String, Object> convertToMap(UsersEntity user) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", user.getUserId());
        map.put("username", user.getUsername());
        map.put("passwordHash", user.getPasswordHash());
        map.put("email", user.getEmail());
        map.put("birthDate", user.getBirthDate());
        map.put("municipalityCode", user.getMunicipalityCode());
        map.put("licenseStatus", user.getLicenseStatus());
        map.put("licenseSurrenderedAt", user.getLicenseSurrenderedAt());
        
        if (user.getSystemField() != null) {
            map.put("sysCreatedAt", user.getSystemField().getSysCreatedAt());
            map.put("sysUpdatedAt", user.getSystemField().getSysUpdatedAt());
        }
        
        return map;
    }
}
