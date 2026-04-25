package io.github.taichi0373.benefit_map.repository.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import io.github.taichi0373.benefit_map.repository.entity.FareDiscountEntity;
import io.github.taichi0373.benefit_map.repository.entity.FareDiscountEntity_;
import io.github.taichi0373.benefit_map.util.ValidateUtils;

/**
 * 運賃割引DAOインターフェース
 * <p>
 * 公共交通事業者ごとの運賃割引情報の登録・更新・削除・検索操作を提供する。
 * </p>
 */
@Dao
@ConfigAutowireable
@SuppressWarnings("PMD.TooManyMethods")
public interface FareDiscountDao {

    /**
     * 主キー検索
     */
    default FareDiscountEntity selectById(String benefit_id, String agency_id) {
        Entityql entityql = new Entityql(Config.get(this));
        FareDiscountEntity_ e = new FareDiscountEntity_();

        return entityql.from(e)
                      .where(c -> c.eq(e.benefitId, benefit_id))
                      .where(c -> c.eq(e.agencyId, agency_id))
                      .fetchOne();
    }

    /**
     * 管理者向けページング検索（各フィールドでフィルター・ソート可）
     *
     * @param offset        オフセット
     * @param limit         取得件数
     * @param benefitId     特典IDの部分一致（null の場合は全件）
     * @param agencyId      事業者IDの部分一致（null の場合は全件）
     * @param discountType  割引種別の部分一致（null の場合は全件）
     * @param discountValue 割引額の完全一致（null の場合は全件）
     * @param sort          ソートフィールド名
     * @param order         ソート順（desc で降順）
     * @return 運賃割引エンティティリスト
     */
    default List<FareDiscountEntity> selectForAdmin(int offset, int limit, String benefitId,
            String agencyId, String discountType, String discountValue, String keyword, String sort, String order) {
        Entityql entityql = new Entityql(Config.get(this));
        FareDiscountEntity_ e = new FareDiscountEntity_();

        return entityql.from(e)
                .where(c -> {
                    if (!ValidateUtils.isNullOrEmpty(keyword)) {
                        c.and(() -> {
                            c.like(e.benefitId, "%" + keyword + "%");
                            c.or(() -> c.like(e.agencyId, "%" + keyword + "%"));
                            c.or(() -> c.like(e.discountType, "%" + keyword + "%"));
                        });
                    }
                    if (!ValidateUtils.isNullOrEmpty(benefitId)) {
                        c.like(e.benefitId, "%" + benefitId + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(agencyId)) {
                        c.like(e.agencyId, "%" + agencyId + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(discountType)) {
                        c.like(e.discountType, "%" + discountType + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(discountValue)) {
                        try {
                            c.eq(e.discountValue, Integer.parseInt(discountValue));
                        } catch (NumberFormatException ignored) {
                            // 無効な数値は無視
                        }
                    }
                })
                .orderBy(c -> {
                    boolean desc = "desc".equalsIgnoreCase(order);
                    switch (sort != null ? sort : "") {
                        case "benefitId" -> { if (desc) c.desc(e.benefitId); else c.asc(e.benefitId); }
                        case "agencyId" -> { if (desc) c.desc(e.agencyId); else c.asc(e.agencyId); }
                        case "discountType" -> { if (desc) c.desc(e.discountType); else c.asc(e.discountType); }
                        case "discountValue" -> { if (desc) c.desc(e.discountValue); else c.asc(e.discountValue); }
                        default -> {
                            c.asc(e.benefitId);
                            c.asc(e.agencyId);
                        }
                    }
                })
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 管理者向け件数カウント（各フィールドでフィルター可）
     *
     * @param benefitId     特典IDの部分一致（null の場合は全件）
     * @param agencyId      事業者IDの部分一致（null の場合は全件）
     * @param discountType  割引種別の部分一致（null の場合は全件）
     * @param discountValue 割引額の完全一致（null の場合は全件）
     * @return 件数
     */
    default long countForAdmin(String benefitId, String agencyId, String discountType, String discountValue, String keyword) {
        Entityql entityql = new Entityql(Config.get(this));
        FareDiscountEntity_ e = new FareDiscountEntity_();

        return entityql.from(e)
                .where(c -> {
                    if (!ValidateUtils.isNullOrEmpty(keyword)) {
                        c.and(() -> {
                            c.like(e.benefitId, "%" + keyword + "%");
                            c.or(() -> c.like(e.agencyId, "%" + keyword + "%"));
                            c.or(() -> c.like(e.discountType, "%" + keyword + "%"));
                        });
                    }
                    if (!ValidateUtils.isNullOrEmpty(benefitId)) {
                        c.like(e.benefitId, "%" + benefitId + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(agencyId)) {
                        c.like(e.agencyId, "%" + agencyId + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(discountType)) {
                        c.like(e.discountType, "%" + discountType + "%");
                    }
                    if (!ValidateUtils.isNullOrEmpty(discountValue)) {
                        try {
                            c.eq(e.discountValue, Integer.parseInt(discountValue));
                        } catch (NumberFormatException ignored) {
                            // 無効な数値は無視
                        }
                    }
                })
                .stream().count();
    }

    /**
     * 特典IDで検索（依存チェック用）
     *
     * @param benefitId 特典ID
     * @return 運賃割引エンティティリスト
     */
    default List<FareDiscountEntity> selectByBenefitId(String benefitId) {
        Entityql entityql = new Entityql(Config.get(this));
        FareDiscountEntity_ e = new FareDiscountEntity_();

        return entityql.from(e)
                .where(c -> c.eq(e.benefitId, benefitId))
                .fetch();
    }

    /**
     * 事業者IDで検索（依存チェック用）
     *
     * @param agencyId 事業者ID
     * @return 運賃割引エンティティリスト
     */
    default List<FareDiscountEntity> selectByAgencyId(String agencyId) {
        Entityql entityql = new Entityql(Config.get(this));
        FareDiscountEntity_ e = new FareDiscountEntity_();

        return entityql.from(e)
                .where(c -> c.eq(e.agencyId, agencyId))
                .fetch();
    }

    /**
     * 登録
     */
    @Insert
    int insert(FareDiscountEntity entity);

    /**
     * 更新
     */
    @Update
    int update(FareDiscountEntity entity);

    /**
     * 削除
     */
    @Delete
    int delete(FareDiscountEntity entity);
}
