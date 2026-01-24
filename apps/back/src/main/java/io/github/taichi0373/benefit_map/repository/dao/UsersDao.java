package io.github.taichi0373.benefit_map.repository.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import io.github.taichi0373.benefit_map.repository.entity.UsersEntity;
import io.github.taichi0373.benefit_map.repository.entity.UsersEntity_;

@Dao
@ConfigAutowireable
@SuppressWarnings("PMD.TooManyMethods")
public interface UsersDao {

    /** 
     * 主キー検索
     */
    default UsersEntity selectById(String userId) {
        Entityql entityql = new Entityql(Config.get(this));
        UsersEntity_ e = new UsersEntity_();

        return entityql.from(e)
                      .where(c -> c.eq(e.userId, userId))
                      .fetchOne();
    }

    // /**
    //  * 自治体コードで検索
    //  */
    // @Select
    // List<UsersEntity> selectByMunicipalityCode(String municipalityCode);

    // /**
    //  * 運転免許所持状況で検索
    //  */
    // @Select
    // List<UsersEntity> selectByLicenseStatus(String licenseStatus);

    // /**
    //  * 年齢で検索（birthDate から計算）
    //  */
    // @Select
    // List<UsersEntity> selectByAgeRange(int minAge, int maxAge);

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
