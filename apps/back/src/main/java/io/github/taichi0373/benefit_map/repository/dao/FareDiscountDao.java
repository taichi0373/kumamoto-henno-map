package io.github.taichi0373.benefit_map.repository.dao;

import java.util.List;

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
     * 管理者向けページング検索
     *
     * @param offset オフセット
     * @param limit  取得件数
     * @return 運賃割引エンティティリスト
     */
    default List<FareDiscountEntity> selectForAdmin(int offset, int limit) {
        Entityql entityql = new Entityql(Config.get(this));
        FareDiscountEntity_ e = new FareDiscountEntity_();

        return entityql.from(e)
                .orderBy(c -> {
                    c.asc(e.benefitId);
                    c.asc(e.agencyId);
                })
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
        FareDiscountEntity_ e = new FareDiscountEntity_();

        return entityql.from(e).stream().count();
    }

    /**
     * 特典IDで検索（依存チェック用）
     *
     * @param benefitId 特典ID
     * @return 運賃割引エンティティリスト
     */
    default List<FareDiscountEntity> selectByBenefitId(String benefitId) {
        Entityql entityql = new Entityql(Config.get(this));
        FareDiscountEntity_ e = new FareDiscountEntity_();

        return entityql.from(e)
                .where(c -> c.eq(e.benefitId, benefitId))
                .fetch();
    }

    /**
     * 事業者IDで検索（依存チェック用）
     *
     * @param agencyId 事業者ID
     * @return 運賃割引エンティティリスト
     */
    default List<FareDiscountEntity> selectByAgencyId(String agencyId) {
        Entityql entityql = new Entityql(Config.get(this));
        FareDiscountEntity_ e = new FareDiscountEntity_();

        return entityql.from(e)
                .where(c -> c.eq(e.agencyId, agencyId))
                .fetch();
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
