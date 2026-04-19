package io.github.taichi0373.benefit_map.repository.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import java.util.List;

import io.github.taichi0373.benefit_map.repository.entity.BenefitCategoryEntity;
import io.github.taichi0373.benefit_map.repository.entity.BenefitCategoryEntity_;
import io.github.taichi0373.benefit_map.util.ValidateUtils;

/**
 * 特典カテゴリDAOインターフェース
 * <p>
 * 特典カテゴリ情報の登録・更新・削除・検索操作を提供する。
 * </p>
 */
@Dao
@ConfigAutowireable
@SuppressWarnings("PMD.TooManyMethods")
public interface BenefitCategoryDao {

    /**
     * 有効カテゴリを表示順で全件取得
     * @return 有効カテゴリ一覧
     */
    default List<BenefitCategoryEntity> selectAllOrdered() {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitCategoryEntity_ e = new BenefitCategoryEntity_();

        return entityql.from(e)
                .where(c -> c.eq(e.isActive, "1"))
                .orderBy(c -> c.asc(e.displayOrder))
                .fetch();
    }

    /**
     * 主キー検索
     */
    default BenefitCategoryEntity selectById(String categoryCd) {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitCategoryEntity_ e = new BenefitCategoryEntity_();

        return entityql.from(e)
                      .where(c -> c.eq(e.categoryCd, categoryCd))
                      .fetchOne();
    }

    // /**
    //  * 全件取得（表示順）
    //  */
    // @Select
    // List<BenefitCategoryEntity> selectAllOrderByDisplayOrder();

    // /**
    //  * 有効な種別のみ取得
    //  */
    // @Select
    // List<BenefitCategoryEntity> selectActive();

    /**
     * 管理者向けページング検索（各フィールドでフィルター・ソート可。有効・無効を含む）
     *
     * @param offset       オフセット
     * @param limit        取得件数
     * @param categoryName カテゴリ名称の部分一致（null の場合は全件）
     * @param categoryCd   カテゴリコードの部分一致（null の場合は全件）
     * @param displayOrder 表示順の完全一致（null の場合は全件）
     * @param sort         ソートフィールド名
     * @param order        ソート順（desc で降順）
     * @return 特典カテゴリエンティティリスト
     */
    default List<BenefitCategoryEntity> selectForAdmin(int offset, int limit, String categoryName,
            String categoryCd, String displayOrder, String sort, String order) {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitCategoryEntity_ e = new BenefitCategoryEntity_();

        return entityql.from(e)
                .where(c -> {
                    if (!ValidateUtils.isNullOrEmpty(categoryName)) {
                        c.like(e.categoryName, "%" + categoryName + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(categoryCd)) {
                        c.like(e.categoryCd, "%" + categoryCd + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(displayOrder)) {
                        try {
                            c.eq(e.displayOrder, Integer.parseInt(displayOrder));
                        } catch (NumberFormatException ignored) {
                            // 無効な数値は無視
                        }
                    }
                })
                .orderBy(c -> {
                    boolean desc = "desc".equalsIgnoreCase(order);
                    switch (sort != null ? sort : "") {
                        case "categoryCd" -> { if (desc) c.desc(e.categoryCd); else c.asc(e.categoryCd); }
                        case "categoryName" -> { if (desc) c.desc(e.categoryName); else c.asc(e.categoryName); }
                        case "displayOrder" -> { if (desc) c.desc(e.displayOrder); else c.asc(e.displayOrder); }
                        default -> c.asc(e.displayOrder);
                    }
                })
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 管理者向け件数カウント（各フィールドでフィルター可）
     *
     * @param categoryName カテゴリ名称の部分一致（null の場合は全件）
     * @param categoryCd   カテゴリコードの部分一致（null の場合は全件）
     * @param displayOrder 表示順の完全一致（null の場合は全件）
     * @return 件数
     */
    default long countForAdmin(String categoryName, String categoryCd, String displayOrder) {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitCategoryEntity_ e = new BenefitCategoryEntity_();

        return entityql.from(e)
                .where(c -> {
                    if (!ValidateUtils.isNullOrEmpty(categoryName)) {
                        c.like(e.categoryName, "%" + categoryName + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(categoryCd)) {
                        c.like(e.categoryCd, "%" + categoryCd + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(displayOrder)) {
                        try {
                            c.eq(e.displayOrder, Integer.parseInt(displayOrder));
                        } catch (NumberFormatException ignored) {
                            // 無効な数値は無視
                        }
                    }
                })
                .stream().count();
    }

    /**
     * 登録
     */
    @Insert
    int insert(BenefitCategoryEntity entity);

    /**
     * 更新
     */
    @Update
    int update(BenefitCategoryEntity entity);

    /**
     * 削除
     */
    @Delete
    int delete(BenefitCategoryEntity entity);
}
