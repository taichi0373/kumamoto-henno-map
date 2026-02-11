package io.github.taichi0373.benefit_map.repository.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import io.github.taichi0373.benefit_map.repository.entity.BenefitEntity;
import io.github.taichi0373.benefit_map.repository.entity.BenefitEntity_;
import io.github.taichi0373.benefit_map.util.ValidateUtils;

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

    /**
     * 特典IDリストで検索
     */
    default List<BenefitEntity> selectByIds(List<String> benefitIds) {
        if (ValidateUtils.isNullOrEmpty(benefitIds)) {
            return List.of();
        }
        
        Entityql entityql = new Entityql(Config.get(this));
        BenefitEntity_ e = new BenefitEntity_();
        
        return entityql.from(e)
                      .where(c -> c.in(e.benefitId, benefitIds))
                      .fetch();
    }

    /**
     * カテゴリコードで検索
     */
    default List<BenefitEntity> selectByCategoryCd(String categoryCd) {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitEntity_ e = new BenefitEntity_();
        
        return entityql.from(e)
                      .where(c -> c.eq(e.categoryCd, categoryCd))
                      .fetch();
    }

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
