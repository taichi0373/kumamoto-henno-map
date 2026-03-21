package io.github.taichi0373.benefit_map.repository.entity;

import java.io.Serial;
import java.io.Serializable;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * 運賃割引条件ビューエンティティ（V_FARE_DISCOUNT_ELIGIBILITY）
 */
@Entity(metamodel = @Metamodel)
@Table(name = "v_fare_discount_eligibility")
@Getter
@Setter
public class FareDiscountEligibilityEntity implements Serializable {

    /** シリアルバージョンUID */
    @Serial
    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // 運賃割引情報
    // -------------------------------------------------------------------------

    /** 特典ID */
    @Id
    @Column(name = "benefit_id")
    private String benefitId;

    /** 事業者ID */
    @Column(name = "agency_id")
    private String agencyId;

    /** 割引種別 */
    @Column(name = "discount_type")
    private String discountType;

    /** 割引値 */
    @Column(name = "discount_value")
    private Integer discountValue;

    // -------------------------------------------------------------------------
    // 利用条件情報（絞り込みに使用）
    // -------------------------------------------------------------------------

    /** 運転免許所持状況 */
    @Column(name = "license_status")
    private String licenseStatus;

    /** 最低年齢 */
    @Column(name = "min_age")
    private Integer minAge;

    /** 最高年齢 */
    @Column(name = "max_age")
    private Integer maxAge;

    /** 対象自治体コード */
    @Column(name = "eligibility_municipality_cd")
    private String eligibilityMunicipalityCd;
}
