package io.github.taichi0373.kumamoto_henno_map.repository.entity;

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
 * 市区町村エンティティ
 * <p>
 * 市区町村の情報を保持するDomaエンティティ。
 * </p>
 */
@Schema(description = "市区町村情報")
@Entity(metamodel = @Metamodel)
@Table(name = "municipality")
@Getter
@Setter
public class MunicipalityEntity implements Serializable {

    /** シリアルバージョンUID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 自治体コード */
    @Schema(description = "自治体コード", example = "43100")
    @Id
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

    /** システム共通フィールド */
    @Schema(hidden = true)
    private SystemField systemField;

}
