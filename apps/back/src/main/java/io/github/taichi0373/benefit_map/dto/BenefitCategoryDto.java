package io.github.taichi0373.benefit_map.dto;

import lombok.Data;

/**
 * 特典カテゴリDTO
 * <p>
 * APIレスポンス用の特典カテゴリ情報。システム管理項目（作成日時・更新日時）は含まない。
 * </p>
 */
@Data
public class BenefitCategoryDto {

    /** 種別コード */
    private String categoryCd;

    /** 種別名称 */
    private String categoryName;

    /** 表示順 */
    private Integer displayOrder;

    /** 有効フラグ */
    private String isActive;
}
