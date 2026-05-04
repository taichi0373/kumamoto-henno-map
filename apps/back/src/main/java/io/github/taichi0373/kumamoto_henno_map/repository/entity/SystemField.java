package io.github.taichi0373.kumamoto_henno_map.repository.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import org.seasar.doma.Column;
import org.seasar.doma.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * システム共通フィールド。
 * 全エンティティに共通する作成日時・更新日時を保持するDomaの埋め込みオブジェクト。
 */
@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
