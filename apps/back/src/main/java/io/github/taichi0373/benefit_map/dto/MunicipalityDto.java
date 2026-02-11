package io.github.taichi0373.benefit_map.dto;

import io.github.taichi0373.benefit_map.repository.entity.SystemField;
import lombok.Data;
@Data
public class MunicipalityDto {

    /** 自治体コード */
    private String municipalityCd;

    /** 自治体名称 */
    private String municipalityName;

    /** 自治体名称かな */
    private String municipalityKana;

    /** システム共通フィールド */
    private SystemField systemField;
}
