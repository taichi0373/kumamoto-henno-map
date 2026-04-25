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
     * 管理者向けページング検索（各フィールドでフィルター・ソート可）
     *
     * @param offset           オフセット
     * @param limit            取得件数
     * @param municipalityName 自治体名称の部分一致（null の場合は全件）
     * @param municipalityCd   自治体コードの部分一致（null の場合は全件）
     * @param municipalityKana 自治体カナの部分一致（null の場合は全件）
     * @param municipalityType 自治体区分の部分一致（null の場合は全件）
     * @param sort             ソートフィールド名
     * @param order            ソート順（desc で降順）
     * @return 自治体エンティティリスト
     */
    default List<MunicipalityEntity> selectForAdmin(int offset, int limit, String municipalityName,
            String municipalityCd, String municipalityKana, String municipalityType,
            String keyword, String sort, String order) {
        Entityql entityql = new Entityql(Config.get(this));
        MunicipalityEntity_ e = new MunicipalityEntity_();

        return entityql.from(e)
                .where(c -> {
                    if (!ValidateUtils.isNullOrEmpty(keyword)) {
                        c.and(() -> {
                            c.like(e.municipalityName, "%" + keyword + "%");
                            c.or(() -> c.like(e.municipalityCd, "%" + keyword + "%"));
                            c.or(() -> c.like(e.municipalityKana, "%" + keyword + "%"));
                            c.or(() -> c.like(e.municipalityType, "%" + keyword + "%"));
                        });
                    }
                    if (!ValidateUtils.isNullOrEmpty(municipalityName)) {
                        c.like(e.municipalityName, "%" + municipalityName + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(municipalityCd)) {
                        c.like(e.municipalityCd, "%" + municipalityCd + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(municipalityKana)) {
                        c.like(e.municipalityKana, "%" + municipalityKana + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(municipalityType)) {
                        c.like(e.municipalityType, "%" + municipalityType + "%");
                    }
                })
                .orderBy(c -> {
                    boolean desc = "desc".equalsIgnoreCase(order);
                    switch (sort != null ? sort : "") {
                        case "municipalityCd" -> { if (desc) c.desc(e.municipalityCd); else c.asc(e.municipalityCd); }
                        case "municipalityName" -> { if (desc) c.desc(e.municipalityName); else c.asc(e.municipalityName); }
                        case "municipalityKana" -> { if (desc) c.desc(e.municipalityKana); else c.asc(e.municipalityKana); }
                        case "municipalityType" -> { if (desc) c.desc(e.municipalityType); else c.asc(e.municipalityType); }
                        default -> c.asc(e.municipalityCd);
                    }
                })
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 管理者向け件数カウント（各フィールドでフィルター可）
     *
     * @param municipalityName 自治体名称の部分一致（null の場合は全件）
     * @param municipalityCd   自治体コードの部分一致（null の場合は全件）
     * @param municipalityKana 自治体カナの部分一致（null の場合は全件）
     * @param municipalityType 自治体区分の部分一致（null の場合は全件）
     * @return 件数
     */
    default long countForAdmin(String municipalityName,
            String municipalityCd, String municipalityKana, String municipalityType,
            String keyword) {
        Entityql entityql = new Entityql(Config.get(this));
        MunicipalityEntity_ e = new MunicipalityEntity_();

        return entityql.from(e)
                .where(c -> {
                    if (!ValidateUtils.isNullOrEmpty(keyword)) {
                        c.and(() -> {
                            c.like(e.municipalityName, "%" + keyword + "%");
                            c.or(() -> c.like(e.municipalityCd, "%" + keyword + "%"));
                            c.or(() -> c.like(e.municipalityKana, "%" + keyword + "%"));
                            c.or(() -> c.like(e.municipalityType, "%" + keyword + "%"));
                        });
                    }
                    if (!ValidateUtils.isNullOrEmpty(municipalityName)) {
                        c.like(e.municipalityName, "%" + municipalityName + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(municipalityCd)) {
                        c.like(e.municipalityCd, "%" + municipalityCd + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(municipalityKana)) {
                        c.like(e.municipalityKana, "%" + municipalityKana + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(municipalityType)) {
                        c.like(e.municipalityType, "%" + municipalityType + "%");
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
