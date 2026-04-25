package io.github.taichi0373.benefit_map.repository.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import io.github.taichi0373.benefit_map.repository.entity.CommunityBusEntity;
import io.github.taichi0373.benefit_map.repository.entity.CommunityBusEntity_;

/**
 * コミュニティバスDAOインターフェース
 * <p>
 * コミュニティバス路線情報の登録・更新・削除・検索操作を提供する。
 * </p>
 */
@Dao
@ConfigAutowireable
@SuppressWarnings("PMD.TooManyMethods")
public interface CommunityBusDao {

    /** 
     * 主キー検索
     */
    default CommunityBusEntity selectById(String routeId) {
        Entityql entityql = new Entityql(Config.get(this));
        CommunityBusEntity_ e = new CommunityBusEntity_();

        return entityql.from(e)
                      .where(c -> c.eq(e.routeId, routeId))
                      .fetchOne();
    }

    // /**
    //  * コミュニティバスIDで検索
    //  */
    // @Select
    // List<CommunityBusEntity> selectByCommunityBusId(String communityBusId);

    // /**
    //  * 路線名あいまい検索
    //  */
    // @Select
    // List<CommunityBusEntity> selectByRouteNameLike(String keyword);

    // /**
    //  * 全件取得（名称順）
    //  */
    // @Select
    // List<CommunityBusEntity> selectAllOrderByName();

    /**
     * 登録
     */
    @Insert
    int insert(CommunityBusEntity entity);

    /**
     * 更新
     */
    @Update
    int update(CommunityBusEntity entity);

    /**
     * 削除
     */
    @Delete
    int delete(CommunityBusEntity entity);
}
