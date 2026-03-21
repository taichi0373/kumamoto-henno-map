package io.github.taichi0373.benefit_map.repository.entity;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * 特典詳細ビューエンティティ（V_BENEFIT_DETAIL）
 * <p>特典・カテゴリ・利用条件を結合したビュー用エンティティ。読み取り専用。</p>
 */
@Entity(metamodel = @Metamodel)
@Table(name = "v_benefit_detail")
@Getter
@Setter
public class BenefitDetailEntity implements Serializable {

    /** シリアルバージョンUID */
    @Serial
    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // 特典情報
    // -------------------------------------------------------------------------

    /** 特典ID */
    @Id
    @Column(name = "benefit_id")
    private String benefitId;

    /** 自治体コード */
    @Column(name = "municipality_cd")
    private String municipalityCd;

    /** 自治体名称 */
    @Column(name = "municipality_name")
    private String municipalityName;

    /** 自治体名称かな */
    @Column(name = "municipality_kana")
    private String municipalityKana;

    /** 自治体区分 */
    @Column(name = "municipality_type")
    private String municipalityType;

    /** 特典名称 */
    @Column(name = "benefit_name")
    private String benefitName;

    /** 特典短縮名称 */
    @Column(name = "benefit_short_name")
    private String benefitShortName;

    /** 特典内容 */
    @Column(name = "benefit_detail")
    private String benefitDetail;

    /** 有効期限 */
    @Column(name = "exp_detail")
    private String expDetail;

    /** 問い合わせ電話番号 */
    @Column(name = "phone_number")
    private String phoneNumber;

    /** 特典URL */
    @Column(name = "benefit_url")
    private String benefitUrl;

    // -------------------------------------------------------------------------
    // カテゴリ情報
    // -------------------------------------------------------------------------

    /** カテゴリコード */
    @Column(name = "category_cd")
    private String categoryCd;

    /** カテゴリ名称 */
    @Column(name = "category_name")
    private String categoryName;

    /** 表示順 */
    @Column(name = "display_order")
    private Integer displayOrder;

    /** カテゴリ有効フラグ（1:有効, 0:無効） */
    @Column(name = "category_is_active")
    private String categoryIsActive;

    // -------------------------------------------------------------------------
    // 利用条件情報
    // -------------------------------------------------------------------------

    /** 利用条件ID */
    @Column(name = "eligibility_id")
    private Long eligibilityId;

    /** 運転免許所持状況 */
    @Column(name = "license_status")
    private String licenseStatus;

    /** 最低年齢 */
    @Column(name = "min_age")
    private Integer minAge;

    /** 最高年齢 */
    @Column(name = "max_age")
    private Integer maxAge;

    /** 利用可能な自治体コード */
    @Column(name = "eligibility_municipality_cd")
    private String eligibilityMunicipalityCd;

    /** 備考 */
    @Column(name = "eligibility_note")
    private String eligibilityNote;

    // -------------------------------------------------------------------------
    // 運賃割引情報
    // -------------------------------------------------------------------------

    /** 事業者ID */
    @Column(name = "agency_id")
    private String agencyId;

    /** 割引種別 */
    @Column(name = "discount_type")
    private String discountType;

    /** 割引値 */
    @Column(name = "discount_value")
    private BigDecimal discountValue;
}
