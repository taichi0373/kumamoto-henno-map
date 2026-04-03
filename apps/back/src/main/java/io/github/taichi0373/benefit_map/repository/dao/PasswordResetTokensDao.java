package io.github.taichi0373.benefit_map.repository.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

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
public interface PasswordResetTokensDao {

    /**
     * トークン文字列で検索
     * @param token リセットトークン
     * @return 該当トークンエンティティ、存在しない場合はnull
     */
    default PasswordResetTokensEntity selectByToken(String token) {
        Entityql entityql = new Entityql(Config.get(this));
        PasswordResetTokensEntity_ e = new PasswordResetTokensEntity_();

        return entityql.from(e)
                      .where(c -> c.eq(e.token, token))
                      .fetchOne();
    }

    /**
     * 登録
     * @param entity 登録するトークンエンティティ
     * @return 登録件数
     */
    @Insert
    int insert(PasswordResetTokensEntity entity);

    /**
     * 更新
     * @param entity 更新するトークンエンティティ
     * @return 更新件数
     */
    @Update
    int update(PasswordResetTokensEntity entity);

}
