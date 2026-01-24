package io.github.taichi0373.benefit_map.repository.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import io.github.taichi0373.benefit_map.repository.entity.BenefitEligibilityEntity;

@Dao
@ConfigAutowireable
@SuppressWarnings("PMD.TooManyMethods")
public interface BenefitEligibilityDao {


    // /**
    //  * 特典IDで検索
    //  */
    // @Select
    // List<BenefitEligibilityEntity> selectByBenefitId(String benefitId);

    // /**
    //  * 自治体コードで検索
    //  */
    // @Select
    // List<BenefitEligibilityEntity> selectByMunicipalityCd(String municipalityCd);

    // /**
    //  * 年齢条件で対象検索
    //  */
    // @Select
    // List<BenefitEligibilityEntity> selectByAge(Integer age);

    /**
     * 登録
     */
    @Insert
    int insert(BenefitEligibilityEntity entity);

    /**
     * 更新
     */
    @Update
    int update(BenefitEligibilityEntity entity);

    /**
     * 削除
     */
    @Delete
    int delete(BenefitEligibilityEntity entity);
}
