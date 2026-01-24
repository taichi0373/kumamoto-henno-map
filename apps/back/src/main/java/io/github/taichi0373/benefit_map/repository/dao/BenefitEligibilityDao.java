package io.github.taichi0373.benefit_map.repository.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
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
     * ユーザー条件に一致する特典IDを検索
     */
    default List<String> selectEligibleBenefitIds(Integer age, String licenseStatus, String municipalityCd) {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitEligibilityEntity_ e = new BenefitEligibilityEntity_();
        
        List<BenefitEligibilityEntity> results = entityql.from(e)
                      .where(c -> {
                          // 年齢条件
                          if (age != null) {
                              c.and(() -> {
                                  c.or(() -> {
                                      c.isNull(e.minAge);
                                      c.eq(e.minAge, "");
                                      c.le(e.minAge, String.valueOf(age));
                                  });
                                  c.or(() -> {
                                      c.isNull(e.maxAge);
                                      c.eq(e.maxAge, "");
                                      c.ge(e.maxAge, String.valueOf(age));
                                  });
                              });
                          }
                          
                          // 運転免許所持状況
                          if (licenseStatus != null && !licenseStatus.isEmpty()) {
                              c.or(() -> {
                                  c.isNull(e.licenseStatus);
                                  c.eq(e.licenseStatus, "");
                                  c.eq(e.licenseStatus, licenseStatus);
                              });
                          }
                          
                          // 自治体コード
                          if (municipalityCd != null && !municipalityCd.isEmpty()) {
                              c.or(() -> {
                                  c.isNull(e.municipalityCd);
                                  c.eq(e.municipalityCd, "");
                                  c.eq(e.municipalityCd, municipalityCd);
                              });
                          }
                      })
                      .fetch();
        
        return results.stream()
                      .map(BenefitEligibilityEntity::getBenefitId)
                      .distinct()
                      .toList();
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
