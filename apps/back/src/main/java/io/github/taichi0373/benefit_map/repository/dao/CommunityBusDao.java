package io.github.taichi0373.benefit_map.repository.dao;

import java.util.List;

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
     * 管理者向けページング検索
     *
     * @param offset オフセット
     * @param limit  取得件数
     * @return コミュニティバスエンティティリスト
     */
    default List<CommunityBusEntity> selectForAdmin(int offset, int limit) {
        Entityql entityql = new Entityql(Config.get(this));
        CommunityBusEntity_ e = new CommunityBusEntity_();

        return entityql.from(e)
                .orderBy(c -> c.asc(e.routeId))
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
        CommunityBusEntity_ e = new CommunityBusEntity_();

        return entityql.from(e).stream().count();
    }

    /**
     * コミュニティバスID（事業者ID）で検索（依存チェック用）
     *
     * @param communityBusId コミュニティバスID
     * @return コミュニティバスエンティティリスト
     */
    default List<CommunityBusEntity> selectByCommunityBusId(String communityBusId) {
        Entityql entityql = new Entityql(Config.get(this));
        CommunityBusEntity_ e = new CommunityBusEntity_();

        return entityql.from(e)
                .where(c -> c.eq(e.communityBusId, communityBusId))
                .fetch();
    }

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
