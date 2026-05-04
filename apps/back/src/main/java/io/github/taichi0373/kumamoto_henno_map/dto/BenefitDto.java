package io.github.taichi0373.kumamoto_henno_map.dto;

import io.github.taichi0373.kumamoto_henno_map.repository.entity.SystemField;
import lombok.Data;
/**
 * 特典情報DTO
 * <p>
 * 特典の詳細情報を保持するデータ転送オブジェクト。
 * </p>
 */
@Data
public class BenefitDto {

    /** 特典ID */
    private String benefitId;

    /** 自治体コード */
    private String municipalityCd;

    /** 自治体名称 */
    private String municipalityName;

    /** カテゴリコード */
    private String categoryCd;

    /** 特典名称 */
    private String benefitName;

    /** 特典短縮名称 */
    private String benefitShortName;

    /** 特典内容 */
    private String benefitDetail;

    /** 有効期限 */
    private String expDetail;

    /** 問い合わせ電話番号 */
    private String phoneNumber;

    /** 特典URL */
    private String benefitUrl;

    /** 運転免許所持状況 */
    private String licenseStatus;

    /** 最低年齢 */
    private Integer minAge;

    /** 最高年齢 */
    private Integer maxAge;

    /** 備考 */
    private String note;

    /** システム共通フィールド */
    private SystemField systemField;
}
