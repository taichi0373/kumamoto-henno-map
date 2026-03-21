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

@Entity(metamodel = @Metamodel)
@Table(name = "municipality")
@Getter
@Setter
public class MunicipalityEntity implements Serializable {

    /** シリアルバージョンUID */
    @Serial
    private static final long serialVersionUID = 1L;
    
    /** 自治体コード */
    @Id
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

    /** システム共通フィールド */
    private SystemField systemField;

}
