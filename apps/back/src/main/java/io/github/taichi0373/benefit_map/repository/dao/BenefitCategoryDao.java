package io.github.taichi0373.benefit_map.repository.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import io.github.taichi0373.benefit_map.repository.entity.BenefitCategoryEntity;
import io.github.taichi0373.benefit_map.repository.entity.BenefitCategoryEntity_;

@Dao
@ConfigAutowireable
@SuppressWarnings("PMD.TooManyMethods")
public interface BenefitCategoryDao {

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
