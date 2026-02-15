package io.github.taichi0373.benefit_map.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.taichi0373.benefit_map.dto.UsersDto;
import io.github.taichi0373.benefit_map.exception.DuplicateUserException;
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
     * @param username ユーザー名
     * @param password パスワード
     * @return ログイン成功時はユーザー情報（パスワードは含まない）、ログイン失敗時はnull
     */
    public UsersEntity loginUser(String username, String password) {
        try {
            UsersEntity user = usersDao.selectByUsername(username);
            if (ValidateUtils.isNullOrEmpty(user)) {
                return null;
            }
            // パスワードの照合
            if (passwordEncoder.matches(password, user.getPasswordHash())) {
                // パスワード以外のユーザー情報を返す
                 UsersEntity usersEntity = new UsersEntity();
                 usersEntity.setUserId(user.getUserId());
                 usersEntity.setUsername(user.getUsername());
                 usersEntity.setEmail(user.getEmail());
                 usersEntity.setBirthDate(user.getBirthDate());
                 usersEntity.setMunicipalityCode(user.getMunicipalityCode());
                 usersEntity.setLicenseStatus(user.getLicenseStatus());
                return usersEntity;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    
    /**
     * 新規登録
     * @param users ユーザ情報
     * @return 登録されたユーザ情報（パスワードは含まない）
     */
    public UsersEntity signupUser(UsersDto users) {
        try {
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
            
            usersDao.insert(newUser);
            return newUser;
        } catch (DataIntegrityViolationException e) {
            // 一意制約違反（ユーザー名重複）の場合
            String message = e.getMessage();
            if (message != null && (message.contains("username") || message.contains("unique"))) {
                throw new DuplicateUserException("このユーザー名は既に使用されています", e);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * ユーザー情報の取得
     * @param userId ユーザーID
     * @return ユーザー情報（パスワードは含まない）
     */
    public UsersEntity getUsersInfo(Long userId) {
        try {
            UsersEntity user = usersDao.selectById(userId);
            if (ValidateUtils.isNullOrEmpty(user)) {
                return null;
            }
            
            // パスワード以外のユーザー情報を返す
            UsersEntity usersEntity = new UsersEntity();
            usersEntity.setUserId(user.getUserId());
            usersEntity.setUsername(user.getUsername());
            usersEntity.setEmail(user.getEmail());
            usersEntity.setBirthDate(user.getBirthDate());
            usersEntity.setMunicipalityCode(user.getMunicipalityCode());
            usersEntity.setLicenseStatus(user.getLicenseStatus());
            return usersEntity;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * ユーザー名によるユーザーの存在確認
     * @param username ユーザー名
     * @return 存在する場合はtrue、存在しない場合はfalse
     */
    public Boolean getUserByUsername(String username) {
        try {
            return usersDao.selectByUsername(username) != null;
        } catch (Exception e) {
            throw new RuntimeException("ユーザー名による検索処理でエラーが発生しました", e);
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
