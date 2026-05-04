package io.github.taichi0373.benefit_map.repository.entity;

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
 * パスワードリセットトークンエンティティ
 * <p>
 * パスワードリセット要求時に生成されるワンタイムトークンを保持するDomaエンティティ。
 * トークンは有効期限（30分）とワンタイム使用制限を持つ。
 * </p>
 */
@Entity(metamodel = @Metamodel)
@Table(name = "password_reset_tokens")
@Getter
@Setter
public class PasswordResetTokensEntity implements Serializable {

    /** シリアルバージョンUID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** トークンID */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(sequence = "password_reset_tokens_token_id_seq")
    @Column(name = "token_id")
    Long tokenId;

    /** ユーザーID */
    @Column(name = "user_id")
    Long userId;

    /** リセットトークン（平文トークンのSHA-256ハッシュ値、64文字hex文字列。DBには平文を保存しない） */
    @Column(name = "token")
    String token;

    /** トークン有効期限 */
    @Column(name = "expires_at")
    LocalDateTime expiresAt;

    /** 使用済みフラグ */
    @Column(name = "used")
    Boolean used;

    /** システム共通フィールド */
    private SystemField systemField;

}
