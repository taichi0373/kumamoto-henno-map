package io.github.taichi0373.benefit_map.repository.entity;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

import io.github.taichi0373.benefit_map.repository.entity.SystemField;

@Entity(metamodel = @Metamodel)
@Table(name = "fare_discount")
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
    private BigDecimal discountValue;

    /** システム共通フィールド */
    private SystemField systemField;

}
