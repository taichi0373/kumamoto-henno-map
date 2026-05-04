package io.github.taichi0373.kumamoto_henno_map.repository.entity;

import java.io.Serial;
import java.io.Serializable;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * 特典カテゴリエンティティ
 * <p>
 * 特典の分類（カテゴリ）情報を保持するDomaエンティティ。
 * </p>
 */
@Entity(metamodel = @Metamodel)
@Table(name = "benefit_category")
@Getter
@Setter
public class BenefitCategoryEntity implements Serializable {

    /** シリアルバージョンUID */
    @Serial
    private static final long serialVersionUID = 1L;;
    
    /** 種別コード */
    @Id
    @Column(name = "category_cd")
    private String categoryCd;

    /** 種別名称 */
    @Column(name = "category_name")
    private String categoryName;

    /** 表示順 */
    @Column(name = "display_order")
    private Integer displayOrder;

    /** 有効フラグ */
    @Column(name = "is_active")
    private String isActive;

    /** システム共通フィールド */
    private SystemField systemField;

}
