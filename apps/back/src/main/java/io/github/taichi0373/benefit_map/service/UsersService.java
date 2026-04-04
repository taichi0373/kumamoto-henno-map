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
            newUser.setLicenseSurrenderedAt(users.getLicenseSurrenderedAt());
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
     * メールアドレスによる重複確認（新規登録用）
     * @param email メールアドレス
     * @return 同一メールアドレスを持つユーザーが存在する場合はtrue
     */
    public Boolean existsByEmail(String email) {
        try {
            return usersDao.existsByEmail(email);
        } catch (Exception e) {
            throw new RuntimeException("メールアドレスによる検索処理でエラーが発生しました", e);
        }
    }

    /**
     * メールアドレスによる重複確認（プロフィール更新用: 自分自身を除外）
     * @param email メールアドレス
     * @param excludeUserId 除外するユーザーID（更新対象ユーザー自身）
     * @return 自分以外に同一メールアドレスを持つユーザーが存在する場合はtrue
     */
    public Boolean existsByEmailExcluding(String email, Long excludeUserId) {
        try {
            return usersDao.existsByEmailExcluding(email, excludeUserId);
        } catch (Exception e) {
            throw new RuntimeException("メールアドレスによる検索処理でエラーが発生しました", e);
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
        dto.setAddress(user.getMunicipalityCd());
        dto.setLicenseStatus(user.getLicenseStatus());
        dto.setLicenseSurrenderedAt(user.getLicenseSurrenderedAt());
        return dto;
    }

    /**
     * パスワード変更
     * <p>
     * 現在のパスワードを検証したうえで新しいパスワードに変更する。
     * DB アクセス等で例外が発生した場合はそのままスローし、呼び出し元で 500 として処理する。
     * </p>
     * @param userId ユーザーID
     * @param currentPassword 現在のパスワード（平文）
     * @param newPassword 新しいパスワード（平文）
     * @return 変更成功時はtrue、現在のパスワードが不一致の場合はfalse、ユーザーが存在しない場合はnull
     * @throws Exception DB アクセスエラー等の内部エラー
     */
    public Boolean changePassword(Long userId, String currentPassword, String newPassword) throws Exception {
        UsersEntity user = usersDao.selectById(userId);
        if (ValidateUtils.isNullOrEmpty(user)) {
            return null;
        }
        // 現在のパスワードを検証
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            return false;
        }
        // 新しいパスワードをハッシュ化して更新
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        LocalDateTime now = LocalDateTime.now();
        SystemField currentSystemField = user.getSystemField();
        LocalDateTime sysCreatedAt = currentSystemField != null && currentSystemField.getSysCreatedAt() != null
                ? currentSystemField.getSysCreatedAt()
                : now;
        user.setSystemField(new SystemField(sysCreatedAt, now));
        usersDao.update(user);
        return true;
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
            LocalDateTime now = LocalDateTime.now();
            
            // ユーザー情報を更新
            existingUser.setEmail(users.getEmail());
            existingUser.setBirthDate(users.getBirthDate());
            existingUser.setMunicipalityCd(users.getAddress());
            existingUser.setLicenseStatus(users.getLicenseStatus());
            existingUser.setLicenseSurrenderedAt(users.getLicenseSurrenderedAt());
            existingUser.setSystemField(new SystemField(existingUser.getSystemField().getSysCreatedAt(), now));
            
            // データベースに更新を保存
            Integer result = usersDao.update(existingUser);
            
            return result;
        } catch (Exception e) {
            return null;
        }

    }
}
