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
     * 検索条件に一致する特典IDを検索
     * @param age 年齢
     * @param licenseStatus 運転免許の所持状況
     * @param municipalityCd 市区町村コード
     * @return 特典IDのリスト
     */
    default List<BenefitEligibilityEntity> selectEligibleBenefitIds(Integer age, String licenseStatus, String municipalityCd) {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitEligibilityEntity_ e = new BenefitEligibilityEntity_();

        return entityql.from(e)
                      .where(c -> {
                          if (age != null) {
                            // MIN_AGE IS NULL または MIN_AGE <= age（最低年齢未満は除外）
                            c.or(() -> {
                                c.isNull(e.minAge);
                                c.le(e.minAge, age);
                            });
                            // MAX_AGE IS NULL または MAX_AGE >= age（最高年齢超過は除外）
                            c.or(() -> {
                                c.isNull(e.maxAge);
                                c.ge(e.maxAge, age);
                            });
                          }
                          // LICENSE_STATUS が NULL（制約なし）または パラメータと一致
                          c.or(() -> {
                              c.isNull(e.licenseStatus);
                              c.eq(e.licenseStatus, licenseStatus);
                          });
                          // MUNICIPALITY_CD が NULL（制約なし）または パラメータと一致
                          c.or(() -> {
                              c.isNull(e.municipalityCd);
                              c.eq(e.municipalityCd, municipalityCd);
                          });
                      })
                      .fetch();
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
