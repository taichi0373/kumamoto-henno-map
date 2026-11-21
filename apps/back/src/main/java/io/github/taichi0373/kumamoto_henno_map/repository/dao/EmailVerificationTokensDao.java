package io.github.taichi0373.kumamoto_henno_map.repository.dao;

import java.time.LocalDateTime;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.QueryDsl;

import io.github.taichi0373.kumamoto_henno_map.repository.entity.EmailVerificationTokensEntity;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.EmailVerificationTokensEntity_;

/**
 * メールアドレス確認トークンDAOインターフェース
 * <p>
 * メールアドレス確認トークンの登録・更新・検索・削除操作を提供する。
 * </p>
 */
@Dao
@ConfigAutowireable
@SuppressWarnings("PMD.TooManyMethods")
public interface EmailVerificationTokensDao {

    /**
     * トークンハッシュ文字列で検索
     * @param tokenHash トークンのSHA-256ハッシュ
     * @return 該当トークンエンティティ、存在しない場合はnull
     */
    default EmailVerificationTokensEntity selectByToken(String tokenHash) {
        QueryDsl queryDsl = new QueryDsl(Config.get(this));
        EmailVerificationTokensEntity_ e = new EmailVerificationTokensEntity_();

        return queryDsl.from(e)
                      .where(c -> c.eq(e.token, tokenHash))
                      .fetchOne();
    }

    /**
     * ユーザーの最新トークンを検索（再送間隔の判定用）
     * @param userId ユーザーID
     * @return 作成日時が最も新しいトークンエンティティ、存在しない場合はnull
     */
    default EmailVerificationTokensEntity selectLatestByUserId(Long userId) {
        QueryDsl queryDsl = new QueryDsl(Config.get(this));
        EmailVerificationTokensEntity_ e = new EmailVerificationTokensEntity_();

        return queryDsl.from(e)
                      .where(c -> c.eq(e.userId, userId))
                      .orderBy(c -> c.desc(e.systemField.sysCreatedAt))
                      .limit(1)
                      .fetchOne();
    }

    /**
     * トークンを使用済みに原子的に更新する
     * <p>
     * used=false かつ expires_at が now より後の場合のみ used=true に更新する。
     * 同時リクエストによる二重使用を防ぐために WHERE 条件付き UPDATE を使用する。
     * </p>
     * @param tokenHash トークンのSHA-256ハッシュ
     * @param now 現在時刻（expires_at との比較に使用）
     * @return 更新件数（1 = 成功、0 = 使用済みまたは期限切れ）
     */
    default int markAsUsedIfValid(String tokenHash, LocalDateTime now) {
        QueryDsl nativeSql = new QueryDsl(Config.get(this));
        EmailVerificationTokensEntity_ e = new EmailVerificationTokensEntity_();
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
     * ユーザーの未使用トークンをすべて使用済みにする
     * <p>
     * 新しいトークンの発行時に呼び出し、最新のリンクのみを有効にする。
     * </p>
     * @param userId ユーザーID
     * @param now 現在時刻
     * @return 更新件数
     */
    default int markAllAsUsedByUserId(Long userId, LocalDateTime now) {
        QueryDsl nativeSql = new QueryDsl(Config.get(this));
        EmailVerificationTokensEntity_ e = new EmailVerificationTokensEntity_();
        return nativeSql.update(e)
                .set(c -> {
                    c.value(e.used, true);
                    c.value(e.systemField.sysUpdatedAt, now);
                })
                .where(c -> {
                    c.eq(e.userId, userId);
                    c.eq(e.used, false);
                })
                .execute();
    }

    /**
     * 期限切れまたは使用済みのトークンを一括削除する
     * <p>
     * 2回に分けることで OR 条件を回避しつつ同等の結果を得る。
     * 定期クリーンアップ処理から呼び出される。
     * </p>
     * @param now 現在時刻（expires_at との比較に使用）
     * @return 削除件数の合計
     */
    default int deleteExpiredOrUsed(LocalDateTime now) {
        QueryDsl nativeSql = new QueryDsl(Config.get(this));
        EmailVerificationTokensEntity_ e = new EmailVerificationTokensEntity_();
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
    int insert(EmailVerificationTokensEntity entity);

}
