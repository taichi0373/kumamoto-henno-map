package io.github.taichi0373.kumamoto_henno_map.repository.entity;

import java.io.Serial;
import java.io.Serializable;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;
import org.seasar.doma.Metamodel;
import org.seasar.doma.SequenceGenerator;
import org.seasar.doma.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * 特典適用条件エンティティ
 * <p>
 * 特典の適用条件（年齢・免許状況・自治体コード）を保持するDomaエンティティ。
 * </p>
 */
@Entity(metamodel = @Metamodel)
@Table(name = "benefit_eligibility")
@Getter
@Setter
public class BenefitEligibilityEntity implements Serializable {

    /** シリアルバージョンUID */
    @Serial
    private static final long serialVersionUID = 1L;
    
    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(sequence = "benefit_eligibility_id_seq")
    @Column(name = "id")
    private Long id;

    /** 特典ID */
    @Column(name = "benefit_id")
    private String benefitId;

    /** 運転免許所持状況 */
    @Column(name = "license_status")
    private String licenseStatus;

    /** 最低年齢 */
    @Column(name = "min_age")
    private Integer minAge;

    /** 最高年齢 */
    @Column(name = "max_age")
    private Integer maxAge;

    /** 対象自治体コード */
    @Column(name = "municipality_cd")
    private String municipalityCd;

    /** 備考 */
    @Column(name = "note")
    private String note;

    /** システム共通フィールド */
    private SystemField systemField;
}
