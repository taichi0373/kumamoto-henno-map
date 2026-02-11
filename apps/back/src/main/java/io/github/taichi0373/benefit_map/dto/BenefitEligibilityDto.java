package io.github.taichi0373.benefit_map.dto;

import io.github.taichi0373.benefit_map.repository.entity.SystemField;
import lombok.Data;
@Data
public class BenefitEligibilityDto {

    /** 特典条件ID */
    private Integer id;

    /** 特典ID */
    private String benefitId;

    /** 運転免許所持状況 */
    private String licenseStatus;

    /** 最低年齢 */
    private Integer minAge;

    /** 最高年齢 */
    private Integer maxAge;

    /** 対象自治体コード */
    private String municipalityCd;

    /** 備考 */
    private String note;

    /** システム共通フィールド */
    private SystemField systemField;
}
