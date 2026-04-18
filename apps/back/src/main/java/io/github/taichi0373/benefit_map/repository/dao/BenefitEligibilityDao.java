package io.github.taichi0373.benefit_map.repository.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import io.github.taichi0373.benefit_map.repository.entity.BenefitEligibilityEntity;
import io.github.taichi0373.benefit_map.repository.entity.BenefitEligibilityEntity_;

/**
 * 特典適用条件DAOインターフェース
 * <p>
 * 特典の適用条件（年齢・免許状況・自治体コード）の登録・更新・削除・検索操作を提供する。
 * </p>
 */
@Dao
@ConfigAutowireable
@SuppressWarnings("PMD.TooManyMethods")
public interface BenefitEligibilityDao {

    /**
     * 特典IDで検索
     */
    default List<BenefitEligibilityEntity> selectByBenefitId(String benefitId) {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitEligibilityEntity_ e = new BenefitEligibilityEntity_();

        return entityql.from(e)
                .where(c -> c.eq(e.benefitId, benefitId))
                .fetch();
    }

    /**
     * IDで検索
     *
     * @param id ID
     * @return 特典適用条件エンティティ
     */
    default BenefitEligibilityEntity selectById(Long id) {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitEligibilityEntity_ e = new BenefitEligibilityEntity_();

        return entityql.from(e)
                .where(c -> c.eq(e.id, id))
                .fetchOne();
    }

    /**
     * 管理者向けページング検索（特典IDでフィルター可）
     *
     * @param offset    オフセット
     * @param limit     取得件数
     * @param benefitId 特典ID（null の場合は全件）
     * @return 特典適用条件エンティティリスト
     */
    default List<BenefitEligibilityEntity> selectForAdmin(int offset, int limit, String benefitId) {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitEligibilityEntity_ e = new BenefitEligibilityEntity_();

        return entityql.from(e)
                .where(c -> {
                    if (benefitId != null && !benefitId.isBlank()) {
                        c.eq(e.benefitId, benefitId);
                    }
                })
                .orderBy(c -> c.asc(e.id))
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 管理者向け件数カウント
     *
     * @param benefitId 特典ID（null の場合は全件）
     * @return 件数
     */
    default long countForAdmin(String benefitId) {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitEligibilityEntity_ e = new BenefitEligibilityEntity_();

        return entityql.from(e)
                .where(c -> {
                    if (benefitId != null && !benefitId.isBlank()) {
                        c.eq(e.benefitId, benefitId);
                    }
                })
                .stream().count();
    }

    /**
     * 登録
     */
    @Insert
    int insert(BenefitEligibilityEntity entity);

    /**
     * 更新
     */
    @Update
    int update(BenefitEligibilityEntity entity);

    /**
     * 削除
     */
    @Delete
    int delete(BenefitEligibilityEntity entity);
}
