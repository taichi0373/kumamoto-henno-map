package io.github.taichi0373.benefit_map.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.taichi0373.benefit_map.dto.UserResponseDto;
import io.github.taichi0373.benefit_map.dto.UsersDto;
import io.github.taichi0373.benefit_map.exception.DuplicateUserException;
import io.github.taichi0373.benefit_map.repository.dao.UsersDao;
import io.github.taichi0373.benefit_map.repository.entity.SystemField;
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
     * @return ログイン成功時はユーザー情報DTO（機微情報を含まない）、ログイン失敗時はnull
     */
    public UserResponseDto loginUser(String username, String password) {
        try {
            UsersEntity user = usersDao.selectByUsername(username);
            if (ValidateUtils.isNullOrEmpty(user)) {
                return null;
            }
            // パスワードの照合
            if (passwordEncoder.matches(password, user.getPasswordHash())) {
                return toUserResponseDto(user);
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
     * @return 登録されたユーザー情報DTO（機微情報を含まない）
     */
    public UserResponseDto signupUser(UsersDto users) {
        try {
            // パスワードをハッシュ化
            String hashedPassword = passwordEncoder.encode(users.getPassword());

            // 新しいユーザーエンティティを作成
            UsersEntity newUser = new UsersEntity();
            newUser.setUsername(users.getUsername());
            newUser.setPasswordHash(hashedPassword);
            newUser.setEmail(users.getEmail());
            newUser.setBirthDate(users.getBirthDate());
            newUser.setMunicipalityCd(users.getAddress());
            newUser.setLicenseStatus(users.getLicenseStatus());
            LocalDateTime now = LocalDateTime.now();
            newUser.setSystemField(new SystemField(now, now));

            usersDao.insert(newUser);
            // insert後にDTOへ変換してpasswordHashを含めずに返す
            return toUserResponseDto(newUser);
        } catch (DataIntegrityViolationException e) {
            // 一意制約違反（ユーザー名重複）の場合は DuplicateUserException に変換してスローする
            throw new DuplicateUserException("このユーザー名は既に使用されています", e);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * ユーザー情報の取得
     * @param userId ユーザーID
     * @return ユーザー情報DTO（機微情報を含まない）
     */
    public UserResponseDto getUsersInfo(Long userId) {
        try {
            UsersEntity user = usersDao.selectById(userId);
            if (ValidateUtils.isNullOrEmpty(user)) {
                return null;
            }
            return toUserResponseDto(user);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * ユーザー名によるユーザーの存在確認
     * @param username ユーザー名
     * @return 存在する場合はtrue、存在しない場合はfalse
     */
    public Boolean existsByUsername(String username) {
        try {
            return usersDao.selectByUsername(username) != null;
        } catch (Exception e) {
            throw new RuntimeException("ユーザー名による検索処理でエラーが発生しました", e);
        }
    }
    
    /**
     * UsersEntityをUserResponseDtoに変換する
     * @param user 変換元エンティティ
     * @return ユーザー情報DTO（機微情報を含まない）
     */
    private UserResponseDto toUserResponseDto(UsersEntity user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setBirthDate(user.getBirthDate());
        dto.setMunicipalityCd(user.getMunicipalityCd());
        dto.setLicenseStatus(user.getLicenseStatus());
        dto.setLicenseSurrenderedAt(user.getLicenseSurrenderedAt());
        return dto;
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
            existingUser.setMunicipalityCd(users.getAddress());
            existingUser.setLicenseStatus(users.getLicenseStatus());
            
            // データベースに更新を保存
            Integer result = usersDao.update(existingUser);
            
            return result;
        } catch (Exception e) {
            return null;
        }

    }
}
