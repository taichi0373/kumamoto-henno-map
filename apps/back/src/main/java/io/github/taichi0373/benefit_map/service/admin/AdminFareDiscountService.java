package io.github.taichi0373.benefit_map.service.admin;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.taichi0373.benefit_map.dto.admin.AdminPagedResponseDto;
import io.github.taichi0373.benefit_map.repository.dao.AgencyDao;
import io.github.taichi0373.benefit_map.repository.dao.BenefitDao;
import io.github.taichi0373.benefit_map.repository.dao.FareDiscountDao;
import io.github.taichi0373.benefit_map.repository.entity.FareDiscountEntity;
import io.github.taichi0373.benefit_map.repository.entity.SystemField;

/**
 * 運賃割引管理サービス
 * <p>
 * 管理者向けの運賃割引CRUD操作を提供する。
 * </p>
 */
@Service
public class AdminFareDiscountService {

    /** 運賃割引DAO */
    @Autowired
    private FareDiscountDao fareDiscountDao;

    /** 特典DAO（外部キー存在チェック用） */
    @Autowired
    private BenefitDao benefitDao;

    /** 事業者DAO（外部キー存在チェック用） */
    @Autowired
    private AgencyDao agencyDao;

    /**
     * 運賃割引一覧をページング取得する
     *
     * @param page ページ番号（0始まり）
     * @param size ページあたり件数
     * @return ページングレスポンス
     */
    public AdminPagedResponseDto<FareDiscountEntity> getAll(int page, int size) {
        int offset = page * size;
        var items = fareDiscountDao.selectForAdmin(offset, size);
        long total = fareDiscountDao.countAll();
        return AdminPagedResponseDto.of(items, total, page, size);
    }

    /**
     * 運賃割引を新規登録する
     *
     * @param entity 登録する運賃割引エンティティ
     * @return 登録した運賃割引エンティティ
     * @throws IllegalArgumentException BENEFIT_ID または AGENCY_ID が存在しない場合
     */
    public FareDiscountEntity create(FareDiscountEntity entity) {
        validateForeignKeys(entity.getBenefitId(), entity.getAgencyId());
        LocalDateTime now = LocalDateTime.now();
        entity.setSystemField(new SystemField(now, now));
        fareDiscountDao.insert(entity);
        return entity;
    }

    /**
     * 運賃割引を更新する
     *
     * @param benefitId 特典ID（複合PK）
     * @param agencyId  事業者ID（複合PK）
     * @param entity    更新する運賃割引エンティティ
     * @return 更新した運賃割引エンティティ
     * @throws NoSuchElementException   運賃割引が存在しない場合
     * @throws IllegalArgumentException BENEFIT_ID または AGENCY_ID が存在しない場合
     */
    public FareDiscountEntity update(String benefitId, String agencyId, FareDiscountEntity entity) {
        FareDiscountEntity existing = fareDiscountDao.selectById(benefitId, agencyId);
        if (existing == null) {
            throw new NoSuchElementException("運賃割引が見つかりません: " + benefitId + "/" + agencyId);
        }
        validateForeignKeys(benefitId, agencyId);
        entity.setBenefitId(benefitId);
        entity.setAgencyId(agencyId);
        LocalDateTime createdAt = existing.getSystemField() != null ? existing.getSystemField().getSysCreatedAt() : null;
        entity.setSystemField(new SystemField(createdAt, LocalDateTime.now()));
        fareDiscountDao.update(entity);
        return entity;
    }

    /**
     * 運賃割引を削除する
     *
     * @param benefitId 特典ID（複合PK）
     * @param agencyId  事業者ID（複合PK）
     * @throws NoSuchElementException 運賃割引が存在しない場合
     */
    public void delete(String benefitId, String agencyId) {
        FareDiscountEntity existing = fareDiscountDao.selectById(benefitId, agencyId);
        if (existing == null) {
            throw new NoSuchElementException("運賃割引が見つかりません: " + benefitId + "/" + agencyId);
        }
        fareDiscountDao.delete(existing);
    }

    /**
     * 外部キー存在チェック
     *
     * @param benefitId 特典ID
     * @param agencyId  事業者ID
     * @throws IllegalArgumentException BENEFIT_ID または AGENCY_ID が存在しない場合
     */
    private void validateForeignKeys(String benefitId, String agencyId) {
        if (benefitDao.selectById(benefitId) == null) {
            throw new IllegalArgumentException("指定された特典IDが存在しません: " + benefitId);
        }
        if (agencyDao.selectById(agencyId) == null) {
            throw new IllegalArgumentException("指定された事業者IDが存在しません: " + agencyId);
        }
    }
}
