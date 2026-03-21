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
@Table(name = "fare_discount")
@Getter
@Setter
public class FareDiscountEntity implements Serializable {

    /** シリアルバージョンUID */
    @Serial
    private static final long serialVersionUID = 1L;
    
    /** 特典ID */
    @Id
    @Column(name = "benefit_id")
    private String benefitId;

    /** 事業者ID */
    @Id
    @Column(name = "agency_id")
    private String agencyId;

    /** 割引種別 */
    @Column(name = "discount_type")
    private String discountType;

    /** 割引値 */
    @Column(name = "discount_value")
    private Integer discountValue;

    /** システム共通フィールド */
    private SystemField systemField;

}
