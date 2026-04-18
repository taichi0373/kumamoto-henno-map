package io.github.taichi0373.benefit_map.service.admin;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.taichi0373.benefit_map.dto.admin.AdminPagedResponseDto;
import io.github.taichi0373.benefit_map.dto.admin.AdminUserResponseDto;
import io.github.taichi0373.benefit_map.repository.dao.UsersDao;
import io.github.taichi0373.benefit_map.repository.entity.SystemField;
import io.github.taichi0373.benefit_map.repository.entity.UsersEntity;

/**
 * ユーザー管理サービス
 * <p>
 * 管理者向けのユーザー参照・更新・削除操作を提供する。
 * 新規作成（POST）は提供しない（ユーザーはセルフ登録のみ）。
 * PASSWORD_HASH と IS_ADMIN は更新対象外。
 * </p>
 */
@Service
public class AdminUsersService {

    /** ユーザーDAO */
    @Autowired
    private UsersDao usersDao;

    /**
     * ユーザー一覧をページング取得する
     *
     * @param page     ページ番号（0始まり）
     * @param size     ページあたり件数
     * @param username ユーザー名フィルター（null可・部分一致）
     * @param email    メールアドレスフィルター（null可・部分一致）
     * @return ページングレスポンス（パスワードハッシュを除く）
     */
    public AdminPagedResponseDto<AdminUserResponseDto> getAll(int page, int size, String username, String email) {
        int offset = page * size;
        var entities = usersDao.selectForAdmin(offset, size, username, email);
        long total = usersDao.countForAdmin(username, email);
        var items = entities.stream().map(this::toResponseDto).toList();
        return AdminPagedResponseDto.of(items, total, page, size);
    }

    /**
     * ユーザーIDでユーザーを取得する
     *
     * @param userId ユーザーID
     * @return ユーザーレスポンスDTO（パスワードハッシュを除く）
     * @throws NoSuchElementException ユーザーが存在しない場合
     */
    public AdminUserResponseDto getById(Long userId) {
        UsersEntity entity = usersDao.selectById(userId);
        if (entity == null) {
            throw new NoSuchElementException("ユーザーが見つかりません: " + userId);
        }
        return toResponseDto(entity);
    }

    /**
     * ユーザー情報を更新する
     * <p>
     * PASSWORD_HASH と IS_ADMIN は更新対象外。
     * </p>
     *
     * @param userId  ユーザーID
     * @param request 更新するユーザーエンティティ（passwordHash・isAdmin は無視される）
     * @return 更新したユーザーレスポンスDTO
     * @throws NoSuchElementException ユーザーが存在しない場合
     */
    public AdminUserResponseDto update(Long userId, UsersEntity request) {
        UsersEntity existing = usersDao.selectById(userId);
        if (existing == null) {
            throw new NoSuchElementException("ユーザーが見つかりません: " + userId);
        }
        // PASSWORD_HASH と IS_ADMIN は既存の値を保持する
        request.setUserId(userId);
        request.setPasswordHash(existing.getPasswordHash());
        request.setIsAdmin(existing.getIsAdmin());
        LocalDateTime createdAt = existing.getSystemField() != null ? existing.getSystemField().getSysCreatedAt() : null;
        request.setSystemField(new SystemField(createdAt, LocalDateTime.now()));
        usersDao.update(request);
        return toResponseDto(request);
    }

    /**
     * ユーザーを削除する
     *
     * @param userId ユーザーID
     * @throws NoSuchElementException ユーザーが存在しない場合
     */
    public void delete(Long userId) {
        UsersEntity existing = usersDao.selectById(userId);
        if (existing == null) {
            throw new NoSuchElementException("ユーザーが見つかりません: " + userId);
        }
        usersDao.delete(existing);
    }

    /**
     * UsersEntity を AdminUserResponseDto に変換する（パスワードハッシュを除く）
     *
     * @param entity ユーザーエンティティ
     * @return ユーザーレスポンスDTO
     */
    private AdminUserResponseDto toResponseDto(UsersEntity entity) {
        AdminUserResponseDto dto = new AdminUserResponseDto();
        dto.setUserId(entity.getUserId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setBirthDate(entity.getBirthDate());
        dto.setMunicipalityCd(entity.getMunicipalityCd());
        dto.setLicenseStatus(entity.getLicenseStatus());
        dto.setLicenseSurrenderedAt(entity.getLicenseSurrenderedAt());
        dto.setIsAdmin(entity.getIsAdmin());
        if (entity.getSystemField() != null) {
            dto.setSysCreatedAt(entity.getSystemField().getSysCreatedAt());
            dto.setSysUpdatedAt(entity.getSystemField().getSysUpdatedAt());
        }
        return dto;
    }
}
