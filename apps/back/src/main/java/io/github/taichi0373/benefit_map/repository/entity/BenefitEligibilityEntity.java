package io.github.taichi0373.benefit_map.repository.entity;

import java.io.Serial;
import java.io.Serializable;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;

import io.github.taichi0373.benefit_map.repository.entity.SystemField;

@Entity
@Table(name = "benefit_eligibility")
public class BenefitEligibilityEntity implements Serializable {

    /** シリアルバージョンUID */
    @Serial
    private static final long serialVersionUID = 1L;
    
    /** ID */
    @Id
    @Column(name = "id")
    private Integer id;

    /** 特典ID */
    @Column(name = "benefit_id")
    private String benefitId;

    /** 運転免許所持状況 */
    @Column(name = "license_status")
    private String licenseStatus;


    /** 最低年齢 */
    @Column(name = "min_age")
    private String minAge;

    /** 最高年齢 */
    @Column(name = "max_age")
    private String maxAge;

    /** 対象自治体コード */
    @Column(name = "municipality_cd")
    private String municipalityCd;

    /** 備考 */
    @Column(name = "note")
    private String note;

    /** システム共通フィールド */
    private SystemField systemField;

}
