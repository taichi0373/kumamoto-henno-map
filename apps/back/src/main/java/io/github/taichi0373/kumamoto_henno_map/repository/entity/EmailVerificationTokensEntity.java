package io.github.taichi0373.kumamoto_henno_map.repository.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

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
 * メールアドレス確認トークンエンティティ
 * <p>
 * 新規登録時・メールアドレス変更時に生成されるワンタイムトークンを保持するDomaエンティティ。
 * トークンは有効期限（24時間）とワンタイム使用制限を持つ。
 * </p>
 */
@Entity(metamodel = @Metamodel)
@Table(name = "email_verification_tokens")
@Getter
@Setter
public class EmailVerificationTokensEntity implements Serializable {

    /** シリアルバージョンUID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** トークンID */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(sequence = "email_verification_tokens_token_id_seq")
    @Column(name = "token_id")
    Long tokenId;

    /** ユーザーID */
    @Column(name = "user_id")
    Long userId;

    /** 確認トークン（平文トークンのSHA-256ハッシュ値、64文字hex文字列。DBには平文を保存しない） */
    @Column(name = "token")
    String token;

    /** 変更後メールアドレス（null:新規登録時の確認） */
    @Column(name = "new_email")
    String newEmail;

    /** トークン有効期限 */
    @Column(name = "expires_at")
    LocalDateTime expiresAt;

    /** 使用済みフラグ */
    @Column(name = "used")
    Boolean used;

    /** システム共通フィールド */
    private SystemField systemField;

}
