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
@Table(name = "community_bus")
@Getter
@Setter
public class CommunityBusEntity implements Serializable {

    /** シリアルバージョンUID */
    @Serial
    private static final long serialVersionUID = 1L;
    
    /** 路線ID */
    @Id
    @Column(name = "route_id")
    private String routeId;

    /** コミュニティバスID */
    @Column(name = "community_bus_id")
    private String communityBusId;

    /** 行先名称 */
    @Column(name = "route_name")
    private String routeName;

    /** システム共通フィールド */
    private SystemField systemField;

}
