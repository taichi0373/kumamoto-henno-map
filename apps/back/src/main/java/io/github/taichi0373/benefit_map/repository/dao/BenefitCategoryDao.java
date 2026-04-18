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
     * 管理者向け全件取得（表示順ソート。有効・無効を含む）
     *
     * @return 特典カテゴリエンティティリスト
     */
    default List<BenefitCategoryEntity> selectAllForAdmin() {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitCategoryEntity_ e = new BenefitCategoryEntity_();

        return entityql.from(e)
                .orderBy(c -> c.asc(e.displayOrder))
                .fetch();
    }

    /**
     * 管理者向け件数カウント
     *
     * @return 件数
     */
    default long countAll() {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitCategoryEntity_ e = new BenefitCategoryEntity_();

        return entityql.from(e).stream().count();
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
