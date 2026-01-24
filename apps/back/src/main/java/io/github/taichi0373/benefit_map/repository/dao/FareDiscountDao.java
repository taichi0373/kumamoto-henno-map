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

import io.github.taichi0373.benefit_map.repository.entity.FareDiscountEntity;
import io.github.taichi0373.benefit_map.repository.entity.FareDiscountEntity_;

@Dao
@ConfigAutowireable
@SuppressWarnings("PMD.TooManyMethods")
public interface FareDiscountDao {

    /** 
     * 主キー検索
     */
    default FareDiscountEntity selectById(String benefit_id, String agency_id) {
        Entityql entityql = new Entityql(Config.get(this));
        FareDiscountEntity_ e = new FareDiscountEntity_();

        return entityql.from(e)
                      .where(c -> c.eq(e.benefitId, benefit_id))
                      .where(c -> c.eq(e.agencyId, agency_id))
                      .fetchOne();
    }

    // /**
    //  * 特典IDで検索
    //  */
    // @Select
    // List<FareDiscountEntity> selectByBenefitId(String benefitId);

    // /**
    //  * 事業者IDで検索
    //  */
    // @Select
    // List<FareDiscountEntity> selectByAgencyId(String agencyId);

    /**
     * 登録
     */
    @Insert
    int insert(FareDiscountEntity entity);

    /**
     * 更新
     */
    @Update
    int update(FareDiscountEntity entity);

    /**
     * 削除
     */
    @Delete
    int delete(FareDiscountEntity entity);
}
