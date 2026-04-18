package io.github.taichi0373.benefit_map.repository.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import io.github.taichi0373.benefit_map.repository.entity.BenefitEntity;
import io.github.taichi0373.benefit_map.repository.entity.BenefitEntity_;
import io.github.taichi0373.benefit_map.util.ValidateUtils;

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
     */
    default BenefitEntity selectById(String benefitId) {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitEntity_ e = new BenefitEntity_();

        return entityql.from(e)
                      .where(c -> c.eq(e.benefitId, benefitId))
                      .fetchOne();
    }

    /**
     * 特典IDリストで検索
     */
    default List<BenefitEntity> selectByIds(List<String> benefitIds) {
        if (ValidateUtils.isNullOrEmpty(benefitIds)) {
            return List.of();
        }
        
        Entityql entityql = new Entityql(Config.get(this));
        BenefitEntity_ e = new BenefitEntity_();
        
        return entityql.from(e)
                      .where(c -> c.in(e.benefitId, benefitIds))
                      .fetch();
    }

    /**
     * カテゴリコードで検索
     */
    default List<BenefitEntity> selectByCategoryCd(String categoryCd) {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitEntity_ e = new BenefitEntity_();
        
        return entityql.from(e)
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
        Entityql entityql = new Entityql(Config.get(this));
        BenefitEntity_ e = new BenefitEntity_();

        return entityql.from(e)
                .where(c -> c.eq(e.municipalityCd, municipalityCd))
                .fetch();
    }

    /**
     * 管理者向けページング検索（自治体コード・カテゴリコードでフィルター可）
     *
     * @param offset       オフセット
     * @param limit        取得件数
     * @param municipalityCd 自治体コード（null の場合は全件）
     * @param categoryCd   カテゴリコード（null の場合は全件）
     * @return 特典エンティティリスト
     */
    default List<BenefitEntity> selectForAdmin(int offset, int limit, String municipalityCd, String categoryCd) {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitEntity_ e = new BenefitEntity_();

        return entityql.from(e)
                .where(c -> {
                    if (!ValidateUtils.isNullOrEmpty(municipalityCd)) {
                        c.eq(e.municipalityCd, municipalityCd);
                    }
                    if (!ValidateUtils.isNullOrEmpty(categoryCd)) {
                        c.eq(e.categoryCd, categoryCd);
                    }
                })
                .orderBy(c -> c.asc(e.benefitId))
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 管理者向け件数カウント（自治体コード・カテゴリコードでフィルター可）
     *
     * @param municipalityCd 自治体コード（null の場合は全件）
     * @param categoryCd   カテゴリコード（null の場合は全件）
     * @return 件数
     */
    default long countForAdmin(String municipalityCd, String categoryCd) {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitEntity_ e = new BenefitEntity_();

        return entityql.from(e)
                .where(c -> {
                    if (!ValidateUtils.isNullOrEmpty(municipalityCd)) {
                        c.eq(e.municipalityCd, municipalityCd);
                    }
                    if (!ValidateUtils.isNullOrEmpty(categoryCd)) {
                        c.eq(e.categoryCd, categoryCd);
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
