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
 * 事業者エンティティ
 * <p>
 * 公共交通事業者（GTFS agency）の情報を保持するDomaエンティティ。
 * </p>
 */
@Entity(metamodel = @Metamodel)
@Table(name = "agency")
@Getter
@Setter
public class AgencyEntity implements Serializable {

    /** シリアルバージョンUID */
    @Serial
    private static final long serialVersionUID = 1L;
    
    /** 事業者ID */
    @Id
    @Column(name = "agency_id")
    private String agencyId;

    /** 事業者名称 */
    @Column(name = "agency_name")
    private String agencyName;

    /** 事業者名称かな */
    @Column(name = "agency_kana")
    private String agencyKana;

    /** 問い合わせ電話番号 */
    @Column(name = "phone_number")
    private String phoneNumber;

    /** 事業者URL */
    @Column(name = "agency_url")
    private String agencyUrl;

    /** 運行事業者ID */
    @Column(name = "operator_id")
    private String operatorId;

    /** システム共通フィールド */
    private SystemField systemField;

}
