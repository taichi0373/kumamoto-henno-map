package io.github.taichi0373.benefit_map.repository.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import io.github.taichi0373.benefit_map.repository.entity.BenefitDetailEntity;
import io.github.taichi0373.benefit_map.repository.entity.BenefitDetailEntity_;
import io.github.taichi0373.benefit_map.util.ValidateUtils;

/**
 * 特典詳細ビューDAO（V_BENEFIT_DETAIL）
 */
@Dao
@ConfigAutowireable
public interface BenefitDetailDao {

    /**
     * 全件取得
     * @return 特典詳細一覧
     */
    default List<BenefitDetailEntity> selectAll() {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitDetailEntity_ e = new BenefitDetailEntity_();

        return entityql.from(e).fetch();
    }

    /**
     * 特典IDで検索
     * @param benefitId 特典ID
     * @return 特典詳細一覧
     */
    default List<BenefitDetailEntity> selectByBenefitId(String benefitId) {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitDetailEntity_ e = new BenefitDetailEntity_();

        return entityql.from(e)
                .where(c -> c.eq(e.benefitId, benefitId))
                .fetch();
    }

    /**
     * カテゴリコードで検索
     * @param categoryCd カテゴリコード
     * @return 特典詳細一覧
     */
    default List<BenefitDetailEntity> selectByCategoryCd(String categoryCd) {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitDetailEntity_ e = new BenefitDetailEntity_();

        return entityql.from(e)
                .where(c -> c.eq(e.categoryCd, categoryCd))
                .fetch();
    }

    /**
     * ユーザーの利用資格条件に一致する運賃割引特典を取得
     * @param age            年齢
     * @param licenseStatus  運転免許所持状況
     * @param municipalityCd 対象自治体コード
     * @return 利用可能な運賃割引特典一覧
     */
    default List<BenefitDetailEntity> selectFareDiscountsEligibleForUser(
            Integer age, String licenseStatus, String municipalityCd) {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitDetailEntity_ e = new BenefitDetailEntity_();

        return entityql.from(e)
                .where(c -> {
                    c.isNotNull(e.agencyId);
                    // 年齢条件
                    if (!ValidateUtils.isNullOrEmpty(age)) {
                        c.and(() -> {
                            c.isNull(e.minAge);
                            c.or(() -> c.le(e.minAge, age));
                        });
                        c.and(() -> {
                            c.isNull(e.maxAge);
                            c.or(() -> c.ge(e.maxAge, age));
                        });
                    }
                    // 免許状況
                    if (!ValidateUtils.isNullOrEmpty(licenseStatus)) {
                        c.and(() -> {
                            c.isNull(e.licenseStatus);
                            c.or(() -> c.eq(e.licenseStatus, licenseStatus));
                        });
                    }
                    // 自治体コード
                    if (!ValidateUtils.isNullOrEmpty(municipalityCd) && municipalityCd.length() >= 2) {
                        c.and(() -> {
                            c.isNull(e.eligibilityMunicipalityCd);
                            c.or(() -> c.eq(e.eligibilityMunicipalityCd, municipalityCd));
                            c.or(() -> c.eq(e.eligibilityMunicipalityCd, municipalityCd.substring(0, 2)));
                        });
                    }
                })
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
        BenefitDetailEntity_ e = new BenefitDetailEntity_();

        return entityql.from(e)
                .where(c -> {
                    if (!ValidateUtils.isNullOrEmpty(age)) {
                        c.and(() -> {
                            c.isNull(e.minAge);
                            c.or(() -> c.le(e.minAge, age));
                        });
                        c.and(() -> {
                            c.isNull(e.maxAge);
                            c.or(() -> c.ge(e.maxAge, age));
                        });
                    }
                    if (!ValidateUtils.isNullOrEmpty(licenseStatus)) {
                        c.eq(e.licenseStatus, licenseStatus);
                    }
                    // 市区町村コードと都道府県コードの両方を考慮して検索
                    if (!ValidateUtils.isNullOrEmpty(municipalityCd) && municipalityCd.length() >= 2) {
                        c.and(() -> {
                            // 市区町村コード
                            c.eq(e.municipalityCd, municipalityCd);
                            // 都道府県コード（上位2桁）
                            c.or(() -> c.eq(e.municipalityCd, municipalityCd.substring(0, 2)));
                        });
                    }
                })
                .fetch();
    }
}
