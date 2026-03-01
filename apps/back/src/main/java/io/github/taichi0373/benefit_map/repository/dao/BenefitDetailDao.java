package io.github.taichi0373.benefit_map.repository.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import io.github.taichi0373.benefit_map.repository.entity.BenefitDetailEntity;
import io.github.taichi0373.benefit_map.repository.entity.BenefitDetailEntity_;

/**
 * 特典詳細ビューDAO（V_BENEFIT_DETAIL）
 * <p>読み取り専用。登録・更新・削除操作は提供しない。</p>
 */
@Dao
@ConfigAutowireable
public interface BenefitDetailDao {

    /**
     * 全件取得
     *
     * @return 特典詳細一覧
     */
    default List<BenefitDetailEntity> selectAll() {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitDetailEntity_ v = new BenefitDetailEntity_();

        return entityql.from(v).fetch();
    }

    /**
     * 特典IDで検索
     * @param benefitId 特典ID
     * @return 特典詳細一覧
     */
    default List<BenefitDetailEntity> selectByBenefitId(String benefitId) {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitDetailEntity_ v = new BenefitDetailEntity_();

        return entityql.from(v)
                       .where(c -> c.eq(v.benefitId, benefitId))
                       .fetch();
    }

    /**
     * カテゴリコードで検索
     * @param categoryCd カテゴリコード
     * @return 特典詳細一覧
     */
    default List<BenefitDetailEntity> selectByCategoryCd(String categoryCd) {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitDetailEntity_ v = new BenefitDetailEntity_();

        return entityql.from(v)
                       .where(c -> c.eq(v.categoryCd, categoryCd))
                       .fetch();
    }

    /**
     * 自治体コードで検索
     * @param municipalityCd 自治体コード
     * @return 特典詳細一覧
     */
    default List<BenefitDetailEntity> selectByMunicipalityCd(String municipalityCd) {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitDetailEntity_ v = new BenefitDetailEntity_();

        return entityql.from(v)
                       .where(c -> c.eq(v.municipalityCd, municipalityCd))
                       .fetch();
    }

    /**
     * 利用資格条件で検索（年齢・運転免許所持状況・自治体コード）
     * @param age            年齢
     * @param licenseStatus  運転免許所持状況
     * @param municipalityCd 対象自治体コード
     * @return 特典詳細一覧
     */
    default List<BenefitDetailEntity> selectEligible(
            Integer age, String licenseStatus, String municipalityCd) {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitDetailEntity_ v = new BenefitDetailEntity_();

        return entityql.from(v)
                       .where(c -> {
                           // MIN_AGE IS NULL または MIN_AGE <= age（最低年齢未満は除外）
                           c.or(() -> {
                               c.isNull(v.minAge);
                               c.le(v.minAge, age);
                           });
                           // MAX_AGE IS NULL または MAX_AGE >= age（最高年齢超過は除外）
                           c.or(() -> {
                               c.isNull(v.maxAge);
                               c.ge(v.maxAge, age);
                           });
                           c.eq(v.licenseStatus, licenseStatus);
                           c.eq(v.eligibilityMunicipalityCd, municipalityCd);
                       })
                       .fetch();
    }
}
