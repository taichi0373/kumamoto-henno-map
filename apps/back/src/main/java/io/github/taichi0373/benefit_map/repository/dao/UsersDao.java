package io.github.taichi0373.benefit_map.repository.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import io.github.taichi0373.benefit_map.repository.entity.UsersEntity;
import io.github.taichi0373.benefit_map.repository.entity.UsersEntity_;

/**
 * ユーザーDAOインターフェース
 * <p>
 * ユーザー情報の登録・更新・削除・検索操作を提供する。
 * </p>
 */
@Dao
@ConfigAutowireable
@SuppressWarnings("PMD.TooManyMethods")
public interface UsersDao {

    /** 
     * 主キー検索
     */
    default UsersEntity selectById(Long userId) {
        Entityql entityql = new Entityql(Config.get(this));
        UsersEntity_ e = new UsersEntity_();

        return entityql.from(e)
                      .where(c -> c.eq(e.userId, userId))
                      .fetchOne();
    }

    /**
     * ユーザー名で検索
     */
    default UsersEntity selectByUsername(String username) {
        Entityql entityql = new Entityql(Config.get(this));
        UsersEntity_ e = new UsersEntity_();
        
        return entityql.from(e)
                      .where(c -> c.eq(e.username, username))
                      .fetchOne();
    }

    /**
     * メールアドレスで検索
     * @param email メールアドレス
     * @return 該当ユーザーエンティティ、存在しない場合はnull
     */
    default UsersEntity selectByEmail(String email) {
        Entityql entityql = new Entityql(Config.get(this));
        UsersEntity_ e = new UsersEntity_();

        return entityql.from(e)
                      .where(c -> c.eq(e.email, email))
                      .fetchOne();
    }

    /**
     * メールアドレスの存在確認（新規登録用）
     * @param email メールアドレス
     * @return 同一メールアドレスを持つユーザーが存在する場合はtrue
     */
    default boolean existsByEmail(String email) {
        return selectByEmail(email) != null;
    }

    /**
     * ユーザー名の存在確認（プロフィール更新用: 自分自身を除外）
     * @param username ユーザー名
     * @param excludeUserId 除外するユーザーID（更新対象ユーザー自身）
     * @return 自分以外に同一ユーザー名を持つユーザーが存在する場合はtrue
     */
    default boolean existsByUsernameExcluding(String username, Long excludeUserId) {
        Entityql entityql = new Entityql(Config.get(this));
        UsersEntity_ e = new UsersEntity_();

        return entityql.from(e)
                      .where(c -> {
                          c.eq(e.username, username);
                          c.ne(e.userId, excludeUserId);
                      })
                      .fetchOne() != null;
    }

    /**
     * メールアドレスの存在確認（プロフィール更新用: 自分自身を除外）
     * @param email メールアドレス
     * @param excludeUserId 除外するユーザーID（更新対象ユーザー自身）
     * @return 自分以外に同一メールアドレスを持つユーザーが存在する場合はtrue
     */
    default boolean existsByEmailExcluding(String email, Long excludeUserId) {
        Entityql entityql = new Entityql(Config.get(this));
        UsersEntity_ e = new UsersEntity_();

        return entityql.from(e)
                      .where(c -> {
                          c.eq(e.email, email);
                          c.ne(e.userId, excludeUserId);
                      })
                      .fetchOne() != null;
    }

    /**
     * 管理者向けページング検索（ユーザー名・メールアドレスでフィルター可）
     *
     * @param offset   オフセット
     * @param limit    取得件数
     * @param username ユーザー名の部分一致（null の場合は全件）
     * @param email    メールアドレスの部分一致（null の場合は全件）
     * @return ユーザーエンティティリスト
     */
    default List<UsersEntity> selectForAdmin(int offset, int limit, String username, String email) {
        Entityql entityql = new Entityql(Config.get(this));
        UsersEntity_ e = new UsersEntity_();

        return entityql.from(e)
                .where(c -> {
                    if (username != null && !username.isBlank()) {
                        c.like(e.username, "%" + username + "%");
                    }
                    if (email != null && !email.isBlank()) {
                        c.like(e.email, "%" + email + "%");
                    }
                })
                .orderBy(c -> c.asc(e.userId))
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 管理者向け件数カウント
     *
     * @param username ユーザー名の部分一致（null の場合は全件）
     * @param email    メールアドレスの部分一致（null の場合は全件）
     * @return 件数
     */
    default long countForAdmin(String username, String email) {
        Entityql entityql = new Entityql(Config.get(this));
        UsersEntity_ e = new UsersEntity_();

        return entityql.from(e)
                .where(c -> {
                    if (username != null && !username.isBlank()) {
                        c.like(e.username, "%" + username + "%");
                    }
                    if (email != null && !email.isBlank()) {
                        c.like(e.email, "%" + email + "%");
                    }
                })
                .stream().count();
    }

    /**
     * 自治体コードで検索（依存チェック用）
     *
     * @param municipalityCd 自治体コード
     * @return 該当ユーザーリスト（空の場合は依存なし）
     */
    default List<UsersEntity> selectByMunicipalityCd(String municipalityCd) {
        Entityql entityql = new Entityql(Config.get(this));
        UsersEntity_ e = new UsersEntity_();

        return entityql.from(e)
                .where(c -> c.eq(e.municipalityCd, municipalityCd))
                .fetch();
    }

    /**
     * 登録
     */
    @Insert
    int insert(UsersEntity entity);

    /**
     * 更新
     */
    @Update
    int update(UsersEntity entity);

    /**
     * 削除
     */
    @Delete
    int delete(UsersEntity entity);
}
