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

import io.github.taichi0373.benefit_map.repository.entity.BenefitEntity;
import io.github.taichi0373.benefit_map.repository.entity.BenefitEntity_;

@Dao
@ConfigAutowireable
@SuppressWarnings("PMD.TooManyMethods")
public interface BenefitDao {

    /** 
     * 主キー検索
     */
    default BenefitEntity selectById(String benefitId) {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitEntity_ e = new BenefitEntity_();

        return entityql.from(e)
                      .where(c -> c.eq(e.benefitId, benefitId))
                      .fetchOne();
    }

    // /**
    //  * 自治体コードで検索
    //  */
    // @Select
    // List<BenefitEntity> selectByMunicipalityCd(String municipalityCd);

    // /**
    //  * カテゴリコードで検索
    //  */
    // @Select
    // List<BenefitEntity> selectByCategoryCd(String categoryCd);

    // /**
    //  * 自治体 × カテゴリ検索
    //  */
    // @Select
    // List<BenefitEntity> selectByMunicipalityAndCategory(String municipalityCd, String categoryCd);

    // /**
    //  * 名称あいまい検索
    //  */
    // @Select
    // List<BenefitEntity> selectByNameLike(String keyword);

    /**
     * 登録
     */
    @Insert
    int insert(BenefitEntity entity);

    /**
     * 更新
     */
    @Update
    int update(BenefitEntity entity);

    /**
     * 削除
     */
    @Delete
    int delete(BenefitEntity entity);
}
