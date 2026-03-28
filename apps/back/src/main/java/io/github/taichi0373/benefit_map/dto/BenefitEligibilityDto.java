package io.github.taichi0373.benefit_map.dto;

import io.github.taichi0373.benefit_map.repository.entity.SystemField;
import lombok.Data;
/**
 * 特典適用条件DTO
 * <p>
 * 特典の適用条件（年齢・免許状況・自治体コード）を保持するデータ転送オブジェクト。
 * </p>
 */
@Data
public class BenefitEligibilityDto {

    /** 特典条件ID */
    private Integer id;

    /** 特典ID */
    private String benefitId;

    /** 年齢 */
    private Integer age;

    /** 最低年齢 */
    private Integer minAge;

    /** 最高年齢 */
    private Integer maxAge;

    /** 運転免許所持状況 */
    private String licenseStatus;

    /** 対象自治体コード */
    private String municipalityCd;

    /** 備考 */
    private String note;

    /** システム共通フィールド */
    private SystemField systemField;
}
