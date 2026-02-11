package io.github.taichi0373.benefit_map.repository.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

import lombok.Getter;
import lombok.Setter;

@Entity(metamodel = @Metamodel)
@Table(name = "users")
@Getter
@Setter
public class UsersEntity implements Serializable {

    /** シリアルバージョンUID */
    @Serial
    private static final long serialVersionUID = 1L;
    
    /** ユーザーID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    Long userId;

    /** ユーザー名 */
    @Column(name = "username")
    String username;

    /** パスワード */
    @Column(name = "password_hash")
    String passwordHash;

    /** メールアドレス */
    @Column(name = "email")
    String email;

    /** 生年月日 */
    @Column(name = "birth_date")
    LocalDate birthDate;

    /** 自治体コード */
    @Column(name = "municipality_code")
    String municipalityCode;

    /** 運転免許所持状況 */
    @Column(name = "license_status")
    String licenseStatus;

    /** 運転免許返納日 */
    @Column(name = "license_surrendered_at")
    LocalDate licenseSurrenderedAt;

    /** システム共通フィールド */
    private SystemField systemField;

}
