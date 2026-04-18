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
import io.github.taichi0373.benefit_map.constants.CodeConstants;
import io.github.taichi0373.benefit_map.util.ValidateUtils;

/**
 * 市区町村DAOインターフェース
 * <p>
 * 市区町村情報の登録・更新・削除・検索操作を提供する。
 * </p>
 */
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
                      .where(c -> c.eq(e.municipalityType, CodeConstants.MunicipalityType.CITY))
                      .orderBy(c -> c.asc(e.municipalityCd))
                      .fetch();
    }


    /**
     * 管理者向けページング検索（自治体名称でフィルター可）
     *
     * @param offset           オフセット
     * @param limit            取得件数
     * @param municipalityName 自治体名称（null の場合は全件）
     * @return 自治体エンティティリスト
     */
    default List<MunicipalityEntity> selectForAdmin(int offset, int limit, String municipalityName) {
        Entityql entityql = new Entityql(Config.get(this));
        MunicipalityEntity_ e = new MunicipalityEntity_();

        return entityql.from(e)
                .where(c -> {
                    if (!ValidateUtils.isNullOrEmpty(municipalityName)) {
                        c.like(e.municipalityName, "%" + municipalityName + "%");
                    }
                })
                .orderBy(c -> c.asc(e.municipalityCd))
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 管理者向け件数カウント（自治体名称でフィルター可）
     *
     * @param municipalityName 自治体名称（null の場合は全件）
     * @return 件数
     */
    default long countForAdmin(String municipalityName) {
        Entityql entityql = new Entityql(Config.get(this));
        MunicipalityEntity_ e = new MunicipalityEntity_();

        return entityql.from(e)
                .where(c -> {
                    if (!ValidateUtils.isNullOrEmpty(municipalityName)) {
                        c.like(e.municipalityName, "%" + municipalityName + "%");
                    }
                })
                .stream().count();
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
