package io.github.taichi0373.benefit_map.repository.dao;

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
