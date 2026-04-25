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
 * リフレッシュトークンエンティティ
 * <p>
 * アクセストークン再発行に使用するリフレッシュトークンを保持するDomaエンティティ。
 * DBにはトークン平文ではなくSHA-256ハッシュのみを保存する。
 * 使用のたびにトークンをローテーションし、再利用を防ぐ。
 * </p>
 */
@Entity(metamodel = @Metamodel)
@Table(name = "refresh_tokens")
@Getter
@Setter
public class RefreshTokensEntity implements Serializable {

    /** シリアルバージョンUID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** トークンID */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(sequence = "refresh_tokens_token_id_seq")
    @Column(name = "token_id")
    Long tokenId;

    /** ユーザーID */
    @Column(name = "user_id")
    Long userId;

    /** リフレッシュトークンのSHA-256ハッシュ（平文はCookieにのみ保持し、DBには保存しない） */
    @Column(name = "token_hash")
    String tokenHash;

    /** トークン有効期限 */
    @Column(name = "expires_at")
    LocalDateTime expiresAt;

    /** 失効フラグ（ローテーション・ログアウト時にtrue） */
    @Column(name = "revoked")
    Boolean revoked;

    /** システム共通フィールド */
    private SystemField systemField;

}
