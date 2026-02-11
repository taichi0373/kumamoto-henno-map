package io.github.taichi0373.benefit_map.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.taichi0373.benefit_map.dto.UsersDto;
import io.github.taichi0373.benefit_map.repository.dao.UsersDao;
import io.github.taichi0373.benefit_map.repository.entity.UsersEntity;
import io.github.taichi0373.benefit_map.util.ValidateUtils;

@Service
public class UsersService {

    /**
     * ユーザー情報DAO
     */
    @Autowired
    private UsersDao usersDao;

    /**
     * パスワードエンコーダー
     */
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    /**
     * ログイン
     */
    public UsersEntity loginUser(String username, String password) {
        try {
            UsersEntity user = usersDao.selectByUsername(username);
            if (ValidateUtils.isNullOrEmpty(user)) {
                return null;
            }
            
            // パスワードの照合
            if (passwordEncoder.matches(password, user.getPasswordHash())) {
                return user;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 新規登録
     */
    public UsersEntity signupUser(UsersDto users) {
        try {
            System.out.println("signupUser users: " + users);
            // 既に同じユーザー名が存在するか確認
            UsersEntity existingUser = usersDao.selectByUsername(users.getUsername());
            if (!ValidateUtils.isNullOrEmpty(existingUser)) {
                return null; // ユーザー名が既に存在する場合はnullを返す
            }
            
            // パスワードをハッシュ化
            String hashedPassword = passwordEncoder.encode(users.getPassword());
            
            // 新しいユーザーエンティティを作成
            UsersEntity newUser = new UsersEntity();
            newUser.setUsername(users.getUsername());
            newUser.setPasswordHash(hashedPassword);
            newUser.setEmail(users.getEmail());
            newUser.setBirthDate(users.getBirthDate());
            newUser.setMunicipalityCode(users.getAddress());
            newUser.setLicenseStatus(users.getLicenseStatus());
            
            System.out.println("signupUser newUser: " + newUser);

            // ユーザーをデータベースに挿入
            int insertCount = usersDao.insert(newUser);
            System.out.println("signupUser insertCount: " + insertCount);
            if (insertCount > 0) {
                return newUser;
            } else {
                return null;
            }
        } catch (Exception e) {
            System.err.println("signupUser error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * ユーザー情報の取得
     */
    public UsersEntity getUsersInfo(Long userId) {
        try {
            return usersDao.selectById(userId);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * ユーザー情報の更新
     */
    public Integer updateUsersInfo(UsersDto users) {
        try {
            UsersEntity existingUser = usersDao.selectById(users.getUserId());
            if (ValidateUtils.isNullOrEmpty(existingUser)) {
                return null; // ユーザーが存在しない場合はnullを返す
            }
            
            // ユーザー情報を更新
            existingUser.setEmail(users.getEmail());
            existingUser.setBirthDate(users.getBirthDate());
            existingUser.setMunicipalityCode(users.getAddress());
            existingUser.setLicenseStatus(users.getLicenseStatus());
            
            // データベースに更新を保存
            Integer result = usersDao.update(existingUser);
            
            return result;
        } catch (Exception e) {
            return null;
        }

    }
}
