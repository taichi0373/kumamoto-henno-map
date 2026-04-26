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

/**
 * 特典適用条件DAOインターフェース
 * <p>
 * 特典の適用条件（年齢・免許状況・自治体コード）の登録・更新・削除・検索操作を提供する。
 * </p>
 */
@Dao
@ConfigAutowireable
@SuppressWarnings("PMD.TooManyMethods")
public interface BenefitEligibilityDao {

    /**
     * 特典IDで検索
     *
     * @param benefitId 特典ID
     * @return 特典適用条件エンティティリスト
     */
    default List<BenefitEligibilityEntity> selectByBenefitId(String benefitId) {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitEligibilityEntity_ e = new BenefitEligibilityEntity_();

        return entityql.from(e)
                .where(c -> c.eq(e.benefitId, benefitId))
                .fetch();
    }

    /**
     * IDで検索
     *
     * @param id ID
     * @return 特典適用条件エンティティ
     */
    default BenefitEligibilityEntity selectById(Long id) {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitEligibilityEntity_ e = new BenefitEligibilityEntity_();

        return entityql.from(e)
                .where(c -> c.eq(e.id, id))
                .fetchOne();
    }

    /**
     * 管理者向けページング検索（各フィールドでフィルター・ソート可）
     *
     * @param offset         オフセット
     * @param limit          取得件数
     * @param benefitId      特典IDの完全一致（null の場合は全件）
     * @param id             IDの完全一致（null の場合は全件）
     * @param licenseStatus  免許状況の部分一致（null の場合は全件）
     * @param minAge         最低年齢の完全一致（null の場合は全件）
     * @param maxAge         最高年齢の完全一致（null の場合は全件）
     * @param municipalityCd 自治体コードの部分一致（null の場合は全件）
     * @param sort           ソートフィールド名
     * @param order          ソート順（desc で降順）
     * @return 特典適用条件エンティティリスト
     */
    default List<BenefitEligibilityEntity> selectForAdmin(int offset, int limit, String benefitId,
            String id, String licenseStatus, String minAge, String maxAge, String municipalityCd,
            String keyword, String sort, String order) {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitEligibilityEntity_ e = new BenefitEligibilityEntity_();

        return entityql.from(e)
                .where(c -> {
                    if (keyword != null && !keyword.isBlank()) {
                        c.and(() -> {
                            c.like(e.benefitId, "%" + keyword + "%");
                            c.or(() -> c.like(e.licenseStatus, "%" + keyword + "%"));
                            c.or(() -> c.like(e.municipalityCd, "%" + keyword + "%"));
                        });
                    }
                    if (benefitId != null && !benefitId.isBlank()) {
                        c.eq(e.benefitId, benefitId);
                    }
                    if (id != null && !id.isBlank()) {
                        try {
                            c.eq(e.id, Long.parseLong(id));
                        } catch (NumberFormatException ignored) {
                            // 無効な数値は無視
                        }
                    }
                    if (licenseStatus != null && !licenseStatus.isBlank()) {
                        c.like(e.licenseStatus, "%" + licenseStatus + "%");
                    }
                    if (minAge != null && !minAge.isBlank()) {
                        try {
                            c.eq(e.minAge, Integer.parseInt(minAge));
                        } catch (NumberFormatException ignored) {
                            // 無効な数値は無視
                        }
                    }
                    if (maxAge != null && !maxAge.isBlank()) {
                        try {
                            c.eq(e.maxAge, Integer.parseInt(maxAge));
                        } catch (NumberFormatException ignored) {
                            // 無効な数値は無視
                        }
                    }
                    if (municipalityCd != null && !municipalityCd.isBlank()) {
                        c.like(e.municipalityCd, "%" + municipalityCd + "%");
                    }
                })
                .orderBy(c -> {
                    boolean desc = "desc".equalsIgnoreCase(order);
                    switch (sort != null ? sort : "") {
                        case "id" -> { if (desc) c.desc(e.id); else c.asc(e.id); }
                        case "benefitId" -> { if (desc) c.desc(e.benefitId); else c.asc(e.benefitId); }
                        case "licenseStatus" -> { if (desc) c.desc(e.licenseStatus); else c.asc(e.licenseStatus); }
                        case "minAge" -> { if (desc) c.desc(e.minAge); else c.asc(e.minAge); }
                        case "maxAge" -> { if (desc) c.desc(e.maxAge); else c.asc(e.maxAge); }
                        case "municipalityCd" -> { if (desc) c.desc(e.municipalityCd); else c.asc(e.municipalityCd); }
                        default -> c.asc(e.id);
                    }
                })
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 管理者向け件数カウント
     *
     * @param benefitId      特典IDの完全一致（null の場合は全件）
     * @param id             IDの完全一致（null の場合は全件）
     * @param licenseStatus  免許状況の部分一致（null の場合は全件）
     * @param minAge         最低年齢の完全一致（null の場合は全件）
     * @param maxAge         最高年齢の完全一致（null の場合は全件）
     * @param municipalityCd 自治体コードの部分一致（null の場合は全件）
     * @return 件数
     */
    default long countForAdmin(String benefitId, String id, String licenseStatus,
            String minAge, String maxAge, String municipalityCd, String keyword) {
        Entityql entityql = new Entityql(Config.get(this));
        BenefitEligibilityEntity_ e = new BenefitEligibilityEntity_();

        return entityql.from(e)
                .where(c -> {
                    if (keyword != null && !keyword.isBlank()) {
                        c.and(() -> {
                            c.like(e.benefitId, "%" + keyword + "%");
                            c.or(() -> c.like(e.licenseStatus, "%" + keyword + "%"));
                            c.or(() -> c.like(e.municipalityCd, "%" + keyword + "%"));
                        });
                    }
                    if (benefitId != null && !benefitId.isBlank()) {
                        c.eq(e.benefitId, benefitId);
                    }
                    if (id != null && !id.isBlank()) {
                        try {
                            c.eq(e.id, Long.parseLong(id));
                        } catch (NumberFormatException ignored) {
                            // 無効な数値は無視
                        }
                    }
                    if (licenseStatus != null && !licenseStatus.isBlank()) {
                        c.like(e.licenseStatus, "%" + licenseStatus + "%");
                    }
                    if (minAge != null && !minAge.isBlank()) {
                        try {
                            c.eq(e.minAge, Integer.parseInt(minAge));
                        } catch (NumberFormatException ignored) {
                            // 無効な数値は無視
                        }
                    }
                    if (maxAge != null && !maxAge.isBlank()) {
                        try {
                            c.eq(e.maxAge, Integer.parseInt(maxAge));
                        } catch (NumberFormatException ignored) {
                            // 無効な数値は無視
                        }
                    }
                    if (municipalityCd != null && !municipalityCd.isBlank()) {
                        c.like(e.municipalityCd, "%" + municipalityCd + "%");
                    }
                })
                .stream().count();
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
