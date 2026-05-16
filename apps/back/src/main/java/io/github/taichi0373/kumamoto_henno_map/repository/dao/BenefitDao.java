package io.github.taichi0373.kumamoto_henno_map.repository.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.QueryDsl;

import io.github.taichi0373.kumamoto_henno_map.repository.entity.BenefitEntity;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.BenefitEntity_;
import io.github.taichi0373.kumamoto_henno_map.util.ValidateUtils;

/**
 * 特典情報DAOインターフェース
 * <p>
 * 特典情報の登録・更新・削除・検索操作を提供する。
 * </p>
 */
@Dao
@ConfigAutowireable
@SuppressWarnings("PMD.TooManyMethods")
public interface BenefitDao {

    /**
     * 主キー検索
     *
     * @param benefitId 特典ID
     * @return 特典エンティティ、存在しない場合はnull
     */
    default BenefitEntity selectById(String benefitId) {
        QueryDsl queryDsl = new QueryDsl(Config.get(this));
        BenefitEntity_ e = new BenefitEntity_();

        return queryDsl.from(e)
                      .where(c -> c.eq(e.benefitId, benefitId))
                      .fetchOne();
    }

    /**
     * 特典IDリストで検索
     *
     * @param benefitIds 特典IDリスト
     * @return 特典エンティティリスト（リストが空の場合は空リストを返す）
     */
    default List<BenefitEntity> selectByIds(List<String> benefitIds) {
        if (ValidateUtils.isNullOrEmpty(benefitIds)) {
            return List.of();
        }
        
        QueryDsl queryDsl = new QueryDsl(Config.get(this));
        BenefitEntity_ e = new BenefitEntity_();
        
        return queryDsl.from(e)
                      .where(c -> c.in(e.benefitId, benefitIds))
                      .fetch();
    }

    /**
     * カテゴリコードで検索
     *
     * @param categoryCd カテゴリコード
     * @return 特典エンティティリスト
     */
    default List<BenefitEntity> selectByCategoryCd(String categoryCd) {
        QueryDsl queryDsl = new QueryDsl(Config.get(this));
        BenefitEntity_ e = new BenefitEntity_();
        
        return queryDsl.from(e)
                      .where(c -> c.eq(e.categoryCd, categoryCd))
                      .fetch();
    }

    /**
     * 自治体コードで検索（依存チェック用）
     *
     * @param municipalityCd 自治体コード
     * @return 特典エンティティリスト
     */
    default List<BenefitEntity> selectByMunicipalityCd(String municipalityCd) {
        QueryDsl queryDsl = new QueryDsl(Config.get(this));
        BenefitEntity_ e = new BenefitEntity_();

        return queryDsl.from(e)
                .where(c -> c.eq(e.municipalityCd, municipalityCd))
                .fetch();
    }

    /**
     * 管理者向けページング検索（各フィールドでフィルター・ソート可）
     *
     * @param offset         オフセット
     * @param limit          取得件数
     * @param municipalityCd 自治体コードの部分一致（null の場合は全件）
     * @param categoryCd     カテゴリコードの部分一致（null の場合は全件）
     * @param benefitId      特典IDの部分一致（null の場合は全件）
     * @param benefitName    特典名称の部分一致（null の場合は全件）
     * @param expDetail      説明詳細の部分一致（null の場合は全件）
     * @param sort           ソートフィールド名
     * @param order          ソート順（desc で降順）
     * @return 特典エンティティリスト
     */
    default List<BenefitEntity> selectForAdmin(int offset, int limit, String municipalityCd, String categoryCd,
            String benefitId, String benefitName, String expDetail, String keyword, String sort, String order) {
        QueryDsl queryDsl = new QueryDsl(Config.get(this));
        BenefitEntity_ e = new BenefitEntity_();

        return queryDsl.from(e)
                .where(c -> {
                    if (!ValidateUtils.isNullOrEmpty(keyword)) {
                        c.and(() -> {
                            c.like(e.benefitId, "%" + keyword + "%");
                            c.or(() -> c.like(e.municipalityCd, "%" + keyword + "%"));
                            c.or(() -> c.like(e.categoryCd, "%" + keyword + "%"));
                            c.or(() -> c.like(e.benefitName, "%" + keyword + "%"));
                            c.or(() -> c.like(e.expDetail, "%" + keyword + "%"));
                        });
                    }
                    if (!ValidateUtils.isNullOrEmpty(municipalityCd)) {
                        c.like(e.municipalityCd, "%" + municipalityCd + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(categoryCd)) {
                        c.like(e.categoryCd, "%" + categoryCd + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(benefitId)) {
                        c.like(e.benefitId, "%" + benefitId + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(benefitName)) {
                        c.like(e.benefitName, "%" + benefitName + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(expDetail)) {
                        c.like(e.expDetail, "%" + expDetail + "%");
                    }
                })
                .orderBy(c -> {
                    boolean desc = "desc".equalsIgnoreCase(order);
                    switch (sort != null ? sort : "") {
                        case "benefitId" -> { if (desc) c.desc(e.benefitId); else c.asc(e.benefitId); }
                        case "municipalityCd" -> { if (desc) c.desc(e.municipalityCd); else c.asc(e.municipalityCd); }
                        case "categoryCd" -> { if (desc) c.desc(e.categoryCd); else c.asc(e.categoryCd); }
                        case "benefitName" -> { if (desc) c.desc(e.benefitName); else c.asc(e.benefitName); }
                        case "expDetail" -> { if (desc) c.desc(e.expDetail); else c.asc(e.expDetail); }
                        default -> c.asc(e.benefitId);
                    }
                })
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 管理者向け件数カウント（各フィールドでフィルター可）
     *
     * @param municipalityCd 自治体コードの部分一致（null の場合は全件）
     * @param categoryCd     カテゴリコードの部分一致（null の場合は全件）
     * @param benefitId      特典IDの部分一致（null の場合は全件）
     * @param benefitName    特典名称の部分一致（null の場合は全件）
     * @param expDetail      説明詳細の部分一致（null の場合は全件）
     * @return 件数
     */
    default long countForAdmin(String municipalityCd, String categoryCd,
            String benefitId, String benefitName, String expDetail, String keyword) {
        QueryDsl queryDsl = new QueryDsl(Config.get(this));
        BenefitEntity_ e = new BenefitEntity_();

        return queryDsl.from(e)
                .where(c -> {
                    if (!ValidateUtils.isNullOrEmpty(keyword)) {
                        c.and(() -> {
                            c.like(e.benefitId, "%" + keyword + "%");
                            c.or(() -> c.like(e.municipalityCd, "%" + keyword + "%"));
                            c.or(() -> c.like(e.categoryCd, "%" + keyword + "%"));
                            c.or(() -> c.like(e.benefitName, "%" + keyword + "%"));
                            c.or(() -> c.like(e.expDetail, "%" + keyword + "%"));
                        });
                    }
                    if (!ValidateUtils.isNullOrEmpty(municipalityCd)) {
                        c.like(e.municipalityCd, "%" + municipalityCd + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(categoryCd)) {
                        c.like(e.categoryCd, "%" + categoryCd + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(benefitId)) {
                        c.like(e.benefitId, "%" + benefitId + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(benefitName)) {
                        c.like(e.benefitName, "%" + benefitName + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(expDetail)) {
                        c.like(e.expDetail, "%" + expDetail + "%");
                    }
                })
                .stream().count();
    }

    /**
     * 登録
     */
    @Insert
    int insert(BenefitEntity entity);

    /**
     * 更新
     */
    @Update
    int update(BenefitEntity entity);

    /**
     * 削除
     */
    @Delete
    int delete(BenefitEntity entity);
}
