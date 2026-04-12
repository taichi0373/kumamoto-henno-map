package io.github.taichi0373.benefit_map.repository.entity;

import java.io.Serial;
import java.io.Serializable;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 特典詳細ビューエンティティ（V_BENEFIT_DETAIL）
 */
@Schema(description = "特典詳細情報（自治体・カテゴリ・利用条件を含む）")
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
    @Schema(description = "特典ID", example = "B001")
    @Id
    @Column(name = "benefit_id")
    private String benefitId;

    /** 自治体コード */
    @Schema(description = "自治体コード", example = "43100")
    @Column(name = "municipality_cd")
    private String municipalityCd;

    /** 自治体名称 */
    @Schema(description = "自治体名称", example = "熊本市")
    @Column(name = "municipality_name")
    private String municipalityName;

    /** 自治体名称かな */
    @Schema(description = "自治体名称かな", example = "くまもとし")
    @Column(name = "municipality_kana")
    private String municipalityKana;

    /** 自治体区分 */
    @Schema(description = "自治体区分（1: 都道府県 / 2: 区 / 3: 市町村）", example = "3")
    @Column(name = "municipality_type")
    private String municipalityType;

    /** 特典名称 */
    @Schema(description = "特典名称", example = "バス運賃割引")
    @Column(name = "benefit_name")
    private String benefitName;

    /** 特典短縮名称 */
    @Schema(description = "特典短縮名称", example = "バス割引")
    @Column(name = "benefit_short_name")
    private String benefitShortName;

    /** 特典内容 */
    @Schema(description = "特典内容", example = "バス運賃を10%割引")
    @Column(name = "benefit_detail")
    private String benefitDetail;

    /** 有効期限 */
    @Schema(description = "有効期限", example = "2025年3月31日まで")
    @Column(name = "exp_detail")
    private String expDetail;

    /** 問い合わせ電話番号 */
    @Schema(description = "問い合わせ電話番号", example = "096-XXX-XXXX")
    @Column(name = "phone_number")
    private String phoneNumber;

    /** 特典URL */
    @Schema(description = "特典URL", example = "https://example.com/benefit")
    @Column(name = "benefit_url")
    private String benefitUrl;

    // -------------------------------------------------------------------------
    // カテゴリ情報
    // -------------------------------------------------------------------------

    /** カテゴリコード */
    @Schema(description = "カテゴリコード", example = "C001")
    @Column(name = "category_cd")
    private String categoryCd;

    /** カテゴリ名称 */
    @Schema(description = "カテゴリ名称", example = "交通")
    @Column(name = "category_name")
    private String categoryName;

    /** 表示順 */
    @Schema(description = "表示順", example = "1")
    @Column(name = "display_order")
    private Integer displayOrder;

    /** カテゴリ有効フラグ（1:有効, 0:無効） */
    @Schema(description = "カテゴリ有効フラグ（\"1\": 有効、\"0\": 無効）", example = "1")
    @Column(name = "category_is_active")
    private String categoryIsActive;

    // -------------------------------------------------------------------------
    // 利用条件情報
    // -------------------------------------------------------------------------

    /** 利用条件ID */
    @Schema(description = "利用条件ID", example = "1")
    @Column(name = "eligibility_id")
    private Long eligibilityId;

    /** 運転免許所持状況 */
    @Schema(description = "運転免許所持状況コード（例: 0: 未所持, 1: 所持, 2: 返納, 3: 失効）", example = "2")
    @Column(name = "license_status")
    private String licenseStatus;

    /** 最低年齢 */
    @Schema(description = "利用可能な最低年齢（null は制限なし）", example = "65", nullable = true)
    @Column(name = "min_age")
    private Integer minAge;

    /** 最高年齢 */
    @Schema(description = "利用可能な最高年齢（null は上限なし）", nullable = true)
    @Column(name = "max_age")
    private Integer maxAge;

    /** 利用可能な自治体コード */
    @Schema(description = "利用条件の対象自治体コード", example = "43100")
    @Column(name = "eligibility_municipality_cd")
    private String eligibilityMunicipalityCd;

    /** 備考 */
    @Schema(description = "利用条件の備考", example = "熊本市内限定", nullable = true)
    @Column(name = "eligibility_note")
    private String eligibilityNote;

}
