package io.github.taichi0373.benefit_map.repository.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import io.github.taichi0373.benefit_map.repository.entity.FareDiscountEligibilityEntity;
import io.github.taichi0373.benefit_map.repository.entity.FareDiscountEligibilityEntity_;
import io.github.taichi0373.benefit_map.util.ValidateUtils;

/**
 * 運賃割引条件ビューDAO（V_FARE_DISCOUNT_ELIGIBILITY）
 */
@Dao
@ConfigAutowireable
public interface FareDiscountEligibilityDao {

    /**
     * ユーザーの利用資格条件に一致する運賃割引特典を取得
     * @param age            年齢
     * @param licenseStatus  運転免許所持状況
     * @param municipalityCd 対象自治体コード
     * @return 利用可能な運賃割引特典一覧
     */
    default List<FareDiscountEligibilityEntity> selectEligibleForUser(
            Integer age, String licenseStatus, String municipalityCd) {
        Entityql entityql = new Entityql(Config.get(this));
        FareDiscountEligibilityEntity_ e = new FareDiscountEligibilityEntity_();

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
                        c.and(() -> {
                            c.isNull(e.licenseStatus);
                            c.or(() -> c.eq(e.licenseStatus, licenseStatus));
                        });
                    }
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
}
