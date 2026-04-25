package io.github.taichi0373.benefit_map.repository.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import io.github.taichi0373.benefit_map.repository.entity.FareDiscountEntity;
import io.github.taichi0373.benefit_map.repository.entity.FareDiscountEntity_;

/**
 * 運賃割引DAOインターフェース
 * <p>
 * 公共交通事業者ごとの運賃割引情報の登録・更新・削除・検索操作を提供する。
 * </p>
 */
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
