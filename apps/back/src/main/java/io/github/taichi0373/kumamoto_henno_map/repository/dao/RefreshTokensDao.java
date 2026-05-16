package io.github.taichi0373.kumamoto_henno_map.repository.dao;

import java.time.LocalDateTime;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.QueryDsl;

import io.github.taichi0373.kumamoto_henno_map.repository.entity.RefreshTokensEntity;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.RefreshTokensEntity_;

/**
 * リフレッシュトークンDAOインターフェース
 * <p>
 * リフレッシュトークンの登録・検索・失効操作を提供する。
 * </p>
 */
@Dao
@ConfigAutowireable
public interface RefreshTokensDao {

    /**
     * トークンハッシュで検索
     * @param tokenHash SHA-256ハッシュ値
     * @return 該当エンティティ、存在しない場合はnull
     */
    default RefreshTokensEntity selectByTokenHash(String tokenHash) {
        QueryDsl queryDsl = new QueryDsl(Config.get(this));
        RefreshTokensEntity_ e = new RefreshTokensEntity_();
        return queryDsl.from(e)
                .where(c -> c.eq(e.tokenHash, tokenHash))
                .fetchOne();
    }

    /**
     * トークンを条件付きで原子的に失効させる（ローテーション・ログアウト共用）
     * <p>
     * {@code revoked=false かつ expires_at > now} の条件付き UPDATE を使用することで、
     * 同一トークンへの並行リクエスト（レース）が発生しても
     * DBレベルで一方のみが1件更新され、他方は0件になる。
     * <ul>
     *   <li>ローテーション時: 更新件数0=競合負け → 新トークン発行不可</li>
     *   <li>ログアウト時: 更新件数0=失効済み/期限切れ → 既に無効化済みなので問題なし</li>
     * </ul>
     * </p>
     * @param tokenHash SHA-256ハッシュ値
     * @param now 現在時刻
     * @return 更新件数（1=成功、0=失効済み/期限切れ/競合負け）
     */
    default int revokeIfValidByTokenHash(String tokenHash, LocalDateTime now) {
        QueryDsl nativeSql = new QueryDsl(Config.get(this));
        RefreshTokensEntity_ e = new RefreshTokensEntity_();
        return nativeSql.update(e)
                .set(c -> {
                    c.value(e.revoked, true);
                    c.value(e.systemField.sysUpdatedAt, now);
                })
                .where(c -> {
                    c.eq(e.tokenHash, tokenHash);
                    c.eq(e.revoked, false);
                    c.gt(e.expiresAt, now);
                })
                .execute();
    }

    /**
     * 期限切れ・失効済みトークンを一括削除する（定期クリーンアップ用）
     * <p>
     * OR 条件で1回の DELETE にまとめることでテーブル走査・ロックを1回に抑える。
     * </p>
     * @param now 現在時刻
     * @return 削除件数
     */
    default int deleteExpiredOrRevoked(LocalDateTime now) {
        QueryDsl nativeSql = new QueryDsl(Config.get(this));
        RefreshTokensEntity_ e = new RefreshTokensEntity_();
        return nativeSql.delete(e)
                .where(c -> c.or(() -> {
                    c.le(e.expiresAt, now);
                    c.eq(e.revoked, true);
                }))
                .execute();
    }

    /**
     * 登録
     * @param entity 登録するエンティティ
     * @return 登録件数
     */
    @Insert
    int insert(RefreshTokensEntity entity);

}
