package io.github.taichi0373.benefit_map.dto;

import io.github.taichi0373.benefit_map.repository.entity.SystemField;
import lombok.Data;
@Data
public class BenefitDto {

    /** 特典ID */
    private String benefitId;

    /** 自治体コード */
    private String municipalityCd;

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

    /** システム共通フィールド */
    private SystemField systemField;
}
