package io.github.taichi0373.benefit_map.repository.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import io.github.taichi0373.benefit_map.repository.entity.AgencyEntity;
import io.github.taichi0373.benefit_map.repository.entity.AgencyEntity_;

@Dao
@ConfigAutowireable
@SuppressWarnings("PMD.TooManyMethods")
public interface AgencyDao {

    /** 
     * 主キー検索
     */
    default AgencyEntity selectById(String agencyId) {
        Entityql entityql = new Entityql(Config.get(this));
        AgencyEntity_ e = new AgencyEntity_();

        return entityql.from(e)
                      .where(c -> c.eq(e.agencyId, agencyId))
                      .fetchOne();
    }

    /**
     * 登録
     */
    @Insert
    int insert(AgencyEntity entity);

    /**
     * 更新
     */
    @Update
    int update(AgencyEntity entity);

    /**
     * 削除
     */
    @Delete
    int delete(AgencyEntity entity);
}
