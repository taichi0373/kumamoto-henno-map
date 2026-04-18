package io.github.taichi0373.benefit_map.repository.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import io.github.taichi0373.benefit_map.repository.entity.AgencyEntity;
import io.github.taichi0373.benefit_map.repository.entity.AgencyEntity_;

/**
 * 事業者情報DAOインターフェース
 * <p>
 * 公共交通事業者（GTFS agency）の登録・更新・削除・検索操作を提供する。
 * </p>
 */
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
     * 全件取得（事業者ID昇順）
     *
     * @return 事業者エンティティリスト
     */
    default List<AgencyEntity> selectAll() {
        Entityql entityql = new Entityql(Config.get(this));
        AgencyEntity_ e = new AgencyEntity_();

        return entityql.from(e)
                .orderBy(c -> c.asc(e.agencyId))
                .fetch();
    }

    /**
     * 管理者向けページング検索
     *
     * @param offset オフセット
     * @param limit  取得件数
     * @return 事業者エンティティリスト
     */
    default List<AgencyEntity> selectForAdmin(int offset, int limit) {
        Entityql entityql = new Entityql(Config.get(this));
        AgencyEntity_ e = new AgencyEntity_();

        return entityql.from(e)
                .orderBy(c -> c.asc(e.agencyId))
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 管理者向け件数カウント
     *
     * @return 件数
     */
    default long countAll() {
        Entityql entityql = new Entityql(Config.get(this));
        AgencyEntity_ e = new AgencyEntity_();

        return entityql.from(e).stream().count();
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
