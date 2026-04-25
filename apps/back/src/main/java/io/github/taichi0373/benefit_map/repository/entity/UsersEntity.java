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
import org.seasar.doma.SequenceGenerator;
import org.seasar.doma.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * ユーザーエンティティ
 * <p>
 * アプリ利用者の情報を保持するDomaエンティティ。
 * </p>
 */
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(sequence = "users_user_id_seq")
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
    @Column(name = "municipality_cd")
    String municipalityCd;

    /** 運転免許所持状況 */
    @Column(name = "license_status")
    String licenseStatus;

    /** 運転免許返納日 */
    @Column(name = "license_surrendered_at")
    LocalDate licenseSurrenderedAt;

    /** 管理者フラグ（1:管理者,0:一般ユーザー） */
    @Column(name = "is_admin")
    String isAdmin;

    /** システム共通フィールド */
    private SystemField systemField;

}
