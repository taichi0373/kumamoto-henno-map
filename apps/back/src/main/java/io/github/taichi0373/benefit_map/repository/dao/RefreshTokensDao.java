package io.github.taichi0373.benefit_map.repository.dao;

import java.time.LocalDateTime;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;
import org.seasar.doma.jdbc.criteria.NativeSql;

import io.github.taichi0373.benefit_map.repository.entity.RefreshTokensEntity;
import io.github.taichi0373.benefit_map.repository.entity.RefreshTokensEntity_;

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
        Entityql entityql = new Entityql(Config.get(this));
        RefreshTokensEntity_ e = new RefreshTokensEntity_();
        return entityql.from(e)
                .where(c -> c.eq(e.tokenHash, tokenHash))
                .fetchOne();
    }

    /**
     * トークンを失効させる（ローテーション・ログアウト時）
     * @param tokenHash SHA-256ハッシュ値
     * @param now 更新日時
     * @return 更新件数
     */
    default int revokeByTokenHash(String tokenHash, LocalDateTime now) {
        NativeSql nativeSql = new NativeSql(Config.get(this));
        RefreshTokensEntity_ e = new RefreshTokensEntity_();
        return nativeSql.update(e)
                .set(c -> {
                    c.value(e.revoked, true);
                    c.value(e.systemField.sysUpdatedAt, now);
                })
                .where(c -> c.eq(e.tokenHash, tokenHash))
                .execute();
    }

    /**
     * 期限切れ・失効済みトークンを一括削除する（定期クリーンアップ用）
     * @param now 現在時刻
     * @return 削除件数の合計
     */
    default int deleteExpiredOrRevoked(LocalDateTime now) {
        NativeSql nativeSql = new NativeSql(Config.get(this));
        RefreshTokensEntity_ e = new RefreshTokensEntity_();
        int count = nativeSql.delete(e)
                .where(c -> c.le(e.expiresAt, now))
                .execute();
        count += nativeSql.delete(e)
                .where(c -> c.eq(e.revoked, true))
                .execute();
        return count;
    }

    /**
     * 登録
     * @param entity 登録するエンティティ
     * @return 登録件数
     */
    @Insert
    int insert(RefreshTokensEntity entity);

}
