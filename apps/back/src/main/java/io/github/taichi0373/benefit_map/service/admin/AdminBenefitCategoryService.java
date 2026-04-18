package io.github.taichi0373.benefit_map.service.admin;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.taichi0373.benefit_map.dto.admin.AdminPagedResponseDto;
import io.github.taichi0373.benefit_map.repository.dao.BenefitCategoryDao;
import io.github.taichi0373.benefit_map.repository.dao.BenefitDao;
import io.github.taichi0373.benefit_map.repository.entity.BenefitCategoryEntity;
import io.github.taichi0373.benefit_map.repository.entity.SystemField;

/**
 * 特典カテゴリ管理サービス
 * <p>
 * 管理者向けの特典カテゴリCRUD操作を提供する。
 * </p>
 */
@Service
public class AdminBenefitCategoryService {

    /** 特典カテゴリDAO */
    @Autowired
    private BenefitCategoryDao benefitCategoryDao;

    /** 特典DAO（削除前依存チェック用） */
    @Autowired
    private BenefitDao benefitDao;

    /**
     * 特典カテゴリ一覧をページング取得する（有効・無効を含む）
     *
     * @param page         ページ番号（0始まり）
     * @param size         ページあたり件数
     * @param categoryName カテゴリ名称フィルター（null の場合は全件）
     * @return ページングレスポンス
     */
    public AdminPagedResponseDto<BenefitCategoryEntity> getAll(int page, int size, String categoryName) {
        int offset = page * size;
        var items = benefitCategoryDao.selectForAdmin(offset, size, categoryName);
        long total = benefitCategoryDao.countForAdmin(categoryName);
        return AdminPagedResponseDto.of(items, total, page, size);
    }

    /**
     * 特典カテゴリを新規登録する
     *
     * @param entity 登録する特典カテゴリエンティティ
     * @return 登録した特典カテゴリエンティティ
     */
    public BenefitCategoryEntity create(BenefitCategoryEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setSystemField(new SystemField(now, now));
        benefitCategoryDao.insert(entity);
        return entity;
    }

    /**
     * 特典カテゴリを更新する
     *
     * @param categoryCd カテゴリコード
     * @param entity     更新する特典カテゴリエンティティ
     * @return 更新した特典カテゴリエンティティ
     * @throws NoSuchElementException カテゴリが存在しない場合
     */
    public BenefitCategoryEntity update(String categoryCd, BenefitCategoryEntity entity) {
        BenefitCategoryEntity existing = benefitCategoryDao.selectById(categoryCd);
        if (existing == null) {
            throw new NoSuchElementException("特典カテゴリが見つかりません: " + categoryCd);
        }
        entity.setCategoryCd(categoryCd);
        LocalDateTime createdAt = existing.getSystemField() != null ? existing.getSystemField().getSysCreatedAt() : null;
        entity.setSystemField(new SystemField(createdAt, LocalDateTime.now()));
        benefitCategoryDao.update(entity);
        return entity;
    }

    /**
     * 特典カテゴリを削除する
     * <p>
     * 紐付く BENEFIT レコードが存在する場合は IllegalStateException をスローする。
     * </p>
     *
     * @param categoryCd カテゴリコード
     * @throws NoSuchElementException カテゴリが存在しない場合
     * @throws IllegalStateException  依存する特典が存在する場合
     */
    public void delete(String categoryCd) {
        BenefitCategoryEntity existing = benefitCategoryDao.selectById(categoryCd);
        if (existing == null) {
            throw new NoSuchElementException("特典カテゴリが見つかりません: " + categoryCd);
        }
        if (!benefitDao.selectByCategoryCd(categoryCd).isEmpty()) {
            throw new IllegalStateException("このカテゴリに紐付く特典が存在するため削除できません");
        }
        benefitCategoryDao.delete(existing);
    }
}
