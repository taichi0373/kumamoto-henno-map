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
@Table(name = "benefit")
@Getter
@Setter
public class BenefitEntity implements Serializable {

    /** シリアルバージョンUID */
    @Serial
    private static final long serialVersionUID = 1L;
    
    /** 特典ID */
    @Id
    @Column(name = "benefit_id")
    private String benefitId;

    /** 自治体コード */
    @Column(name = "municipality_cd")
    private String municipalityCd;

    /** カテゴリコード */
    @Column(name = "category_cd")
    private String categoryCd;

    /** 特典名称 */
    @Column(name = "benefit_name")
    private String benefitName;

    /** 特典短縮名称 */
    @Column(name = "benefit_short_name")
    private String benefitShortName;

    /** 特典内容 */
    @Column(name = "benefit_detail")
    private String benefitDetail;

    /** 有効期限 */
    @Column(name = "exp_detail")
    private String expDetail;

    /** 問い合わせ電話番号 */
    @Column(name = "phone_number")
    private String phoneNumber;

    /** 特典URL */
    @Column(name = "benefit_url")
    private String benefitUrl;

    /** システム共通フィールド */
    private SystemField systemField;

}
