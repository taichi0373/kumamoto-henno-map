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
import io.github.taichi0373.benefit_map.util.ValidateUtils;

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
     *
     * @param agencyId 事業者ID
     * @return 事業者エンティティ、存在しない場合はnull
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
     * 管理者向けページング検索（各フィールドでフィルター・ソート可）
     *
     * @param offset      オフセット
     * @param limit       取得件数
     * @param agencyName  事業者名称の部分一致（null の場合は全件）
     * @param agencyId    事業者IDの部分一致（null の場合は全件）
     * @param agencyKana  事業者カナの部分一致（null の場合は全件）
     * @param phoneNumber 電話番号の部分一致（null の場合は全件）
     * @param operatorId  オペレーターIDの部分一致（null の場合は全件）
     * @param sort        ソートフィールド名
     * @param order       ソート順（desc で降順）
     * @return 事業者エンティティリスト
     */
    default List<AgencyEntity> selectForAdmin(int offset, int limit, String agencyName,
            String agencyId, String agencyKana, String phoneNumber, String operatorId,
            String keyword, String sort, String order) {
        Entityql entityql = new Entityql(Config.get(this));
        AgencyEntity_ e = new AgencyEntity_();

        return entityql.from(e)
                .where(c -> {
                    if (!ValidateUtils.isNullOrEmpty(keyword)) {
                        c.and(() -> {
                            c.like(e.agencyId, "%" + keyword + "%");
                            c.or(() -> c.like(e.agencyName, "%" + keyword + "%"));
                            c.or(() -> c.like(e.agencyKana, "%" + keyword + "%"));
                            c.or(() -> c.like(e.phoneNumber, "%" + keyword + "%"));
                            c.or(() -> c.like(e.operatorId, "%" + keyword + "%"));
                        });
                    }
                    if (!ValidateUtils.isNullOrEmpty(agencyName)) {
                        c.like(e.agencyName, "%" + agencyName + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(agencyId)) {
                        c.like(e.agencyId, "%" + agencyId + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(agencyKana)) {
                        c.like(e.agencyKana, "%" + agencyKana + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(phoneNumber)) {
                        c.like(e.phoneNumber, "%" + phoneNumber + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(operatorId)) {
                        c.like(e.operatorId, "%" + operatorId + "%");
                    }
                })
                .orderBy(c -> {
                    boolean desc = "desc".equalsIgnoreCase(order);
                    switch (sort != null ? sort : "") {
                        case "agencyId" -> { if (desc) c.desc(e.agencyId); else c.asc(e.agencyId); }
                        case "agencyName" -> { if (desc) c.desc(e.agencyName); else c.asc(e.agencyName); }
                        case "agencyKana" -> { if (desc) c.desc(e.agencyKana); else c.asc(e.agencyKana); }
                        case "phoneNumber" -> { if (desc) c.desc(e.phoneNumber); else c.asc(e.phoneNumber); }
                        case "operatorId" -> { if (desc) c.desc(e.operatorId); else c.asc(e.operatorId); }
                        default -> c.asc(e.agencyId);
                    }
                })
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 管理者向け件数カウント（各フィールドでフィルター可）
     *
     * @param agencyName  事業者名称の部分一致（null の場合は全件）
     * @param agencyId    事業者IDの部分一致（null の場合は全件）
     * @param agencyKana  事業者カナの部分一致（null の場合は全件）
     * @param phoneNumber 電話番号の部分一致（null の場合は全件）
     * @param operatorId  オペレーターIDの部分一致（null の場合は全件）
     * @return 件数
     */
    default long countForAdmin(String agencyName,
            String agencyId, String agencyKana, String phoneNumber, String operatorId, String keyword) {
        Entityql entityql = new Entityql(Config.get(this));
        AgencyEntity_ e = new AgencyEntity_();

        return entityql.from(e)
                .where(c -> {
                    if (!ValidateUtils.isNullOrEmpty(keyword)) {
                        c.and(() -> {
                            c.like(e.agencyId, "%" + keyword + "%");
                            c.or(() -> c.like(e.agencyName, "%" + keyword + "%"));
                            c.or(() -> c.like(e.agencyKana, "%" + keyword + "%"));
                            c.or(() -> c.like(e.phoneNumber, "%" + keyword + "%"));
                            c.or(() -> c.like(e.operatorId, "%" + keyword + "%"));
                        });
                    }
                    if (!ValidateUtils.isNullOrEmpty(agencyName)) {
                        c.like(e.agencyName, "%" + agencyName + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(agencyId)) {
                        c.like(e.agencyId, "%" + agencyId + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(agencyKana)) {
                        c.like(e.agencyKana, "%" + agencyKana + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(phoneNumber)) {
                        c.like(e.phoneNumber, "%" + phoneNumber + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(operatorId)) {
                        c.like(e.operatorId, "%" + operatorId + "%");
                    }
                })
                .stream().count();
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
