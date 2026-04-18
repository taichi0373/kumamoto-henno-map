package io.github.taichi0373.benefit_map.service.admin;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.taichi0373.benefit_map.dto.admin.AdminPagedResponseDto;
import io.github.taichi0373.benefit_map.repository.dao.BenefitDao;
import io.github.taichi0373.benefit_map.repository.dao.MunicipalityDao;
import io.github.taichi0373.benefit_map.repository.dao.UsersDao;
import io.github.taichi0373.benefit_map.repository.entity.MunicipalityEntity;
import io.github.taichi0373.benefit_map.repository.entity.SystemField;

/**
 * 自治体管理サービス
 * <p>
 * 管理者向けの自治体CRUD操作を提供する。
 * </p>
 */
@Service
public class AdminMunicipalityService {

    /** 自治体DAO */
    @Autowired
    private MunicipalityDao municipalityDao;

    /** 特典DAO（削除前依存チェック用） */
    @Autowired
    private BenefitDao benefitDao;

    /** ユーザーDAO（削除前依存チェック用） */
    @Autowired
    private UsersDao usersDao;

    /**
     * 自治体一覧をページング取得する
     *
     * @param page             ページ番号（0始まり）
     * @param size             ページあたり件数
     * @param municipalityName 自治体名称フィルター（null の場合は全件）
     * @return ページングレスポンス
     */
    public AdminPagedResponseDto<MunicipalityEntity> getAll(int page, int size, String municipalityName) {
        int offset = page * size;
        var items = municipalityDao.selectForAdmin(offset, size, municipalityName);
        long total = municipalityDao.countForAdmin(municipalityName);
        return AdminPagedResponseDto.of(items, total, page, size);
    }

    /**
     * 自治体を新規登録する
     *
     * @param entity 登録する自治体エンティティ
     * @return 登録した自治体エンティティ
     */
    public MunicipalityEntity create(MunicipalityEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setSystemField(new SystemField(now, now));
        municipalityDao.insert(entity);
        return entity;
    }

    /**
     * 自治体を更新する
     *
     * @param municipalityCd 自治体コード
     * @param entity         更新する自治体エンティティ
     * @return 更新した自治体エンティティ
     * @throws NoSuchElementException 自治体が存在しない場合
     */
    public MunicipalityEntity update(String municipalityCd, MunicipalityEntity entity) {
        MunicipalityEntity existing = municipalityDao.selectById(municipalityCd);
        if (existing == null) {
            throw new NoSuchElementException("自治体が見つかりません: " + municipalityCd);
        }
        entity.setMunicipalityCd(municipalityCd);
        LocalDateTime createdAt = existing.getSystemField() != null ? existing.getSystemField().getSysCreatedAt() : null;
        entity.setSystemField(new SystemField(createdAt, LocalDateTime.now()));
        municipalityDao.update(entity);
        return entity;
    }

    /**
     * 自治体を削除する
     * <p>
     * 紐付く BENEFIT または USERS が存在する場合は IllegalStateException をスローする。
     * </p>
     *
     * @param municipalityCd 自治体コード
     * @throws NoSuchElementException 自治体が存在しない場合
     * @throws IllegalStateException  依存するレコードが存在する場合
     */
    public void delete(String municipalityCd) {
        MunicipalityEntity existing = municipalityDao.selectById(municipalityCd);
        if (existing == null) {
            throw new NoSuchElementException("自治体が見つかりません: " + municipalityCd);
        }
        if (!benefitDao.selectByMunicipalityCd(municipalityCd).isEmpty()) {
            throw new IllegalStateException("この自治体に紐付く特典が存在するため削除できません");
        }
        if (!usersDao.selectByMunicipalityCd(municipalityCd).isEmpty()) {
            throw new IllegalStateException("この自治体に居住するユーザーが存在するため削除できません");
        }
        municipalityDao.delete(existing);
    }
}
