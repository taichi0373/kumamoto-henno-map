package io.github.taichi0373.benefit_map.service.admin;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.taichi0373.benefit_map.dto.admin.AdminPagedResponseDto;
import io.github.taichi0373.benefit_map.repository.dao.BenefitDao;
import io.github.taichi0373.benefit_map.repository.dao.BenefitEligibilityDao;
import io.github.taichi0373.benefit_map.repository.dao.FareDiscountDao;
import io.github.taichi0373.benefit_map.repository.entity.BenefitEntity;
import io.github.taichi0373.benefit_map.repository.entity.SystemField;

/**
 * 特典管理サービス
 * <p>
 * 管理者向けの特典CRUD操作とビジネスロジックを提供する。
 * </p>
 */
@Service
public class AdminBenefitService {

    /** 特典DAO */
    @Autowired
    private BenefitDao benefitDao;

    /** 特典適用条件DAO（削除前依存チェック用） */
    @Autowired
    private BenefitEligibilityDao benefitEligibilityDao;

    /** 運賃割引DAO（削除前依存チェック用） */
    @Autowired
    private FareDiscountDao fareDiscountDao;

    /**
     * 特典一覧をページング取得する
     *
     * @param page           ページ番号（0始まり）
     * @param size           ページあたり件数
     * @param municipalityCd 自治体コードフィルター（null可）
     * @param categoryCd     カテゴリコードフィルター（null可）
     * @return ページングレスポンス
     */
    public AdminPagedResponseDto<BenefitEntity> getAll(int page, int size, String municipalityCd, String categoryCd) {
        int offset = page * size;
        var items = benefitDao.selectForAdmin(offset, size, municipalityCd, categoryCd);
        long total = benefitDao.countForAdmin(municipalityCd, categoryCd);
        return AdminPagedResponseDto.of(items, total, page, size);
    }

    /**
     * 特典を新規登録する
     *
     * @param entity 登録する特典エンティティ
     * @return 登録した特典エンティティ
     */
    public BenefitEntity create(BenefitEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setSystemField(new SystemField(now, now));
        benefitDao.insert(entity);
        return entity;
    }

    /**
     * 特典を更新する
     *
     * @param benefitId 特典ID
     * @param entity    更新する特典エンティティ
     * @return 更新した特典エンティティ
     * @throws NoSuchElementException 特典が存在しない場合
     */
    public BenefitEntity update(String benefitId, BenefitEntity entity) {
        BenefitEntity existing = benefitDao.selectById(benefitId);
        if (existing == null) {
            throw new NoSuchElementException("特典が見つかりません: " + benefitId);
        }
        entity.setBenefitId(benefitId);
        LocalDateTime createdAt = existing.getSystemField() != null ? existing.getSystemField().getSysCreatedAt() : null;
        entity.setSystemField(new SystemField(createdAt, LocalDateTime.now()));
        benefitDao.update(entity);
        return entity;
    }

    /**
     * 特典を削除する
     * <p>
     * BENEFIT_ELIGIBILITY または FARE_DISCOUNT に依存するレコードが存在する場合は
     * IllegalStateException をスローする。
     * </p>
     *
     * @param benefitId 特典ID
     * @throws NoSuchElementException  特典が存在しない場合
     * @throws IllegalStateException   依存レコードが存在する場合
     */
    public void delete(String benefitId) {
        BenefitEntity existing = benefitDao.selectById(benefitId);
        if (existing == null) {
            throw new NoSuchElementException("特典が見つかりません: " + benefitId);
        }
        if (!benefitEligibilityDao.selectByBenefitId(benefitId).isEmpty()) {
            throw new IllegalStateException("この特典に紐付く特典条件が存在するため削除できません");
        }
        if (!fareDiscountDao.selectByBenefitId(benefitId).isEmpty()) {
            throw new IllegalStateException("この特典に紐付く運賃割引が存在するため削除できません");
        }
        benefitDao.delete(existing);
    }
}
