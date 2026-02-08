package io.github.taichi0373.benefit_map.repository.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import org.seasar.doma.Column;
import org.seasar.doma.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/** 
 * システム共通フィールド 
 */

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class SystemField implements Serializable {

    /** シリアルバージョンUID */
    @Serial
    private static final long serialVersionUID = 1L;
    
    /**
     * 作成日時
     */
    @Column(name = "sys_created_at")
    LocalDateTime sysCreatedAt;

    /**
     * 更新日時
     */
    @Column(name = "sys_updated_at")
    LocalDateTime sysUpdatedAt;

}
