package io.github.taichi0373.kumamoto_henno_map.repository.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.QueryDsl;

import io.github.taichi0373.kumamoto_henno_map.repository.entity.CommunityBusEntity;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.CommunityBusEntity_;
import io.github.taichi0373.kumamoto_henno_map.util.ValidateUtils;

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
     *
     * @param routeId 路線ID
     * @return コミュニティバスエンティティ、存在しない場合はnull
     */
    default CommunityBusEntity selectById(String routeId) {
        QueryDsl queryDsl = new QueryDsl(Config.get(this));
        CommunityBusEntity_ e = new CommunityBusEntity_();

        return queryDsl.from(e)
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
     * 管理者向けページング検索（各フィールドでフィルター・ソート可）
     *
     * @param offset         オフセット
     * @param limit          取得件数
     * @param routeName      行先名称の部分一致（null の場合は全件）
     * @param routeId        路線IDの部分一致（null の場合は全件）
     * @param communityBusId コミュニティバスIDの部分一致（null の場合は全件）
     * @param sort           ソートフィールド名
     * @param order          ソート順（desc で降順）
     * @return コミュニティバスエンティティリスト
     */
    default List<CommunityBusEntity> selectForAdmin(int offset, int limit, String routeName,
            String routeId, String communityBusId, String keyword, String sort, String order) {
        QueryDsl queryDsl = new QueryDsl(Config.get(this));
        CommunityBusEntity_ e = new CommunityBusEntity_();

        return queryDsl.from(e)
                .where(c -> {
                    if (!ValidateUtils.isNullOrEmpty(keyword)) {
                        c.and(() -> {
                            c.like(e.routeId, "%" + keyword + "%");
                            c.or(() -> c.like(e.communityBusId, "%" + keyword + "%"));
                            c.or(() -> c.like(e.routeName, "%" + keyword + "%"));
                        });
                    }
                    if (!ValidateUtils.isNullOrEmpty(routeName)) {
                        c.like(e.routeName, "%" + routeName + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(routeId)) {
                        c.like(e.routeId, "%" + routeId + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(communityBusId)) {
                        c.like(e.communityBusId, "%" + communityBusId + "%");
                    }
                })
                .orderBy(c -> {
                    boolean desc = "desc".equalsIgnoreCase(order);
                    switch (sort != null ? sort : "") {
                        case "routeId" -> { if (desc) c.desc(e.routeId); else c.asc(e.routeId); }
                        case "communityBusId" -> { if (desc) c.desc(e.communityBusId); else c.asc(e.communityBusId); }
                        case "routeName" -> { if (desc) c.desc(e.routeName); else c.asc(e.routeName); }
                        default -> c.asc(e.routeId);
                    }
                })
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 管理者向け件数カウント（各フィールドでフィルター可）
     *
     * @param routeName      行先名称の部分一致（null の場合は全件）
     * @param routeId        路線IDの部分一致（null の場合は全件）
     * @param communityBusId コミュニティバスIDの部分一致（null の場合は全件）
     * @return 件数
     */
    default long countForAdmin(String routeName, String routeId, String communityBusId, String keyword) {
        QueryDsl queryDsl = new QueryDsl(Config.get(this));
        CommunityBusEntity_ e = new CommunityBusEntity_();

        return queryDsl.from(e)
                .where(c -> {
                    if (!ValidateUtils.isNullOrEmpty(keyword)) {
                        c.and(() -> {
                            c.like(e.routeId, "%" + keyword + "%");
                            c.or(() -> c.like(e.communityBusId, "%" + keyword + "%"));
                            c.or(() -> c.like(e.routeName, "%" + keyword + "%"));
                        });
                    }
                    if (!ValidateUtils.isNullOrEmpty(routeName)) {
                        c.like(e.routeName, "%" + routeName + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(routeId)) {
                        c.like(e.routeId, "%" + routeId + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(communityBusId)) {
                        c.like(e.communityBusId, "%" + communityBusId + "%");
                    }
                })
                .stream().count();
    }

    /**
     * コミュニティバスID（事業者ID）で検索（依存チェック用）
     *
     * @param communityBusId コミュニティバスID
     * @return コミュニティバスエンティティリスト
     */
    default List<CommunityBusEntity> selectByCommunityBusId(String communityBusId) {
        QueryDsl queryDsl = new QueryDsl(Config.get(this));
        CommunityBusEntity_ e = new CommunityBusEntity_();

        return queryDsl.from(e)
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
