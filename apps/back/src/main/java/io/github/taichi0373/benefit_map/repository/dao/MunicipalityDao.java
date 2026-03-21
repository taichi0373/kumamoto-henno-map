package io.github.taichi0373.benefit_map.repository.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import io.github.taichi0373.benefit_map.repository.entity.MunicipalityEntity;
import io.github.taichi0373.benefit_map.repository.entity.MunicipalityEntity_;
import io.github.taichi0373.benefit_map.constants.codeConstants;

@Dao
@ConfigAutowireable
@SuppressWarnings("PMD.TooManyMethods")
public interface MunicipalityDao {

    /** 
     * 自治体コードから市区町村を取得
     */
    default MunicipalityEntity selectById(String municipalityCd) {
        Entityql entityql = new Entityql(Config.get(this));
        MunicipalityEntity_ e = new MunicipalityEntity_();

        return entityql.from(e)
                      .where(c -> c.eq(e.municipalityCd, municipalityCd))
                      .fetchOne();
    }

    /**
     * 市町村の自治体CDを全件取得
     */
    default List<MunicipalityEntity> selectAllOrderByCd() {
        Entityql entityql = new Entityql(Config.get(this));
        MunicipalityEntity_ e = new MunicipalityEntity_();

        return entityql.from(e)
                      .where(c -> c.eq(e.municipalityType, codeConstants.MunicipalityType.CITY))
                      .orderBy(c -> c.asc(e.municipalityCd))
                      .fetch();
    }


    /**
     * 登録
     */
    @Insert
    int insert(MunicipalityEntity entity);

    /**
     * 更新
     */
    @Update
    int update(MunicipalityEntity entity);

    /**
     * 削除
     */
    @Delete
    int delete(MunicipalityEntity entity);
}
