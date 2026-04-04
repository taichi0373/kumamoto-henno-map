package io.github.taichi0373.benefit_map.repository.dao;

import java.time.LocalDateTime;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;
import org.seasar.doma.jdbc.criteria.NativeSql;

import io.github.taichi0373.benefit_map.repository.entity.PasswordResetTokensEntity;
import io.github.taichi0373.benefit_map.repository.entity.PasswordResetTokensEntity_;

/**
 * パスワードリセットトークンDAOインターフェース
 * <p>
 * パスワードリセットトークンの登録・更新・検索操作を提供する。
 * </p>
 */
@Dao
@ConfigAutowireable
@SuppressWarnings("PMD.TooManyMethods")
public interface PasswordResetTokensDao {

    /**
     * トークンハッシュ文字列で検索
     * @param tokenHash トークンのSHA-256ハッシュ
     * @return 該当トークンエンティティ、存在しない場合はnull
     */
    default PasswordResetTokensEntity selectByToken(String tokenHash) {
        Entityql entityql = new Entityql(Config.get(this));
        PasswordResetTokensEntity_ e = new PasswordResetTokensEntity_();

        return entityql.from(e)
                      .where(c -> c.eq(e.token, tokenHash))
                      .fetchOne();
    }

    /**
     * トークンを使用済みに原子的に更新する
     * <p>
     * used=false かつ expires_at が now より後の場合のみ used=true に更新する。
     * 同時リクエストによる二重使用を防ぐために WHERE 条件付き UPDATE を使用する。
     * 更新件数が 0 の場合、トークンが既に使用済みまたは期限切れであることを示す。
     * </p>
     * @param tokenHash トークンのSHA-256ハッシュ
     * @param now 現在時刻（expires_at との比較に使用）
     * @return 更新件数（1 = 成功、0 = 使用済みまたは期限切れ）
     */
    default int markAsUsedIfValid(String tokenHash, LocalDateTime now) {
        NativeSql nativeSql = new NativeSql(Config.get(this));
        PasswordResetTokensEntity_ e = new PasswordResetTokensEntity_();
        return nativeSql.update(e)
                .set(c -> {
                    c.value(e.used, true);
                    c.value(e.systemField.sysUpdatedAt, now);
                })
                .where(c -> {
                    c.eq(e.token, tokenHash);
                    c.eq(e.used, false);
                    c.gt(e.expiresAt, now);
                })
                .execute();
    }

    /**
     * 期限切れまたは使用済みのトークンを一括削除する
     * <p>
     * ① expires_at が now 以前のトークン（期限切れ）を削除する。
     * ② used=true のトークン（使用済み・まだ期限内）を削除する。
     * 2回に分けることで OR 条件を回避しつつ同等の結果を得る。
     * 定期クリーンアップ処理から呼び出される。
     * </p>
     * @param now 現在時刻（expires_at との比較に使用）
     * @return 削除件数の合計
     */
    default int deleteExpiredOrUsed(LocalDateTime now) {
        NativeSql nativeSql = new NativeSql(Config.get(this));
        PasswordResetTokensEntity_ e = new PasswordResetTokensEntity_();
        int count = nativeSql.delete(e)
                .where(c -> c.le(e.expiresAt, now))
                .execute();
        count += nativeSql.delete(e)
                .where(c -> c.eq(e.used, true))
                .execute();
        return count;
    }

    /**
     * 登録
     * @param entity 登録するトークンエンティティ
     * @return 登録件数
     */
    @Insert
    int insert(PasswordResetTokensEntity entity);

}
