package io.github.taichi0373.benefit_map.repository.dao;

import java.time.LocalDate;
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
     * 管理者向けページング検索（各フィールドでフィルター・ソート可）
     *
     * @param offset         オフセット
     * @param limit          取得件数
     * @param username       ユーザー名の部分一致（null の場合は全件）
     * @param email          メールアドレスの部分一致（null の場合は全件）
     * @param userId         ユーザーIDの完全一致（null の場合は全件）
     * @param birthDate      生年月日の完全一致（null の場合は全件）
     * @param municipalityCd 自治体コードの部分一致（null の場合は全件）
     * @param licenseStatus  免許状況の部分一致（null の場合は全件）
     * @param sort           ソートフィールド名
     * @param order          ソート順（desc で降順）
     * @return ユーザーエンティティリスト
     */
    default List<UsersEntity> selectForAdmin(int offset, int limit, String username, String email,
            String userId, String birthDate, String municipalityCd, String licenseStatus,
            String sort, String order) {
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
                    if (userId != null && !userId.isBlank()) {
                        try {
                            c.eq(e.userId, Long.parseLong(userId));
                        } catch (NumberFormatException ignored) {
                            // 無効な数値は無視
                        }
                    }
                    if (birthDate != null && !birthDate.isBlank()) {
                        try {
                            c.eq(e.birthDate, LocalDate.parse(birthDate));
                        } catch (Exception ignored) {
                            // 無効な日付は無視
                        }
                    }
                    if (municipalityCd != null && !municipalityCd.isBlank()) {
                        c.like(e.municipalityCd, "%" + municipalityCd + "%");
                    }
                    if (licenseStatus != null && !licenseStatus.isBlank()) {
                        c.like(e.licenseStatus, "%" + licenseStatus + "%");
                    }
                })
                .orderBy(c -> {
                    boolean desc = "desc".equalsIgnoreCase(order);
                    switch (sort != null ? sort : "") {
                        case "userId" -> { if (desc) c.desc(e.userId); else c.asc(e.userId); }
                        case "username" -> { if (desc) c.desc(e.username); else c.asc(e.username); }
                        case "email" -> { if (desc) c.desc(e.email); else c.asc(e.email); }
                        case "birthDate" -> { if (desc) c.desc(e.birthDate); else c.asc(e.birthDate); }
                        case "municipalityCd" -> { if (desc) c.desc(e.municipalityCd); else c.asc(e.municipalityCd); }
                        case "licenseStatus" -> { if (desc) c.desc(e.licenseStatus); else c.asc(e.licenseStatus); }
                        default -> c.asc(e.userId);
                    }
                })
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 管理者向け件数カウント
     *
     * @param username       ユーザー名の部分一致（null の場合は全件）
     * @param email          メールアドレスの部分一致（null の場合は全件）
     * @param userId         ユーザーIDの完全一致（null の場合は全件）
     * @param birthDate      生年月日の完全一致（null の場合は全件）
     * @param municipalityCd 自治体コードの部分一致（null の場合は全件）
     * @param licenseStatus  免許状況の部分一致（null の場合は全件）
     * @return 件数
     */
    default long countForAdmin(String username, String email,
            String userId, String birthDate, String municipalityCd, String licenseStatus) {
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
                    if (userId != null && !userId.isBlank()) {
                        try {
                            c.eq(e.userId, Long.parseLong(userId));
                        } catch (NumberFormatException ignored) {
                            // 無効な数値は無視
                        }
                    }
                    if (birthDate != null && !birthDate.isBlank()) {
                        try {
                            c.eq(e.birthDate, LocalDate.parse(birthDate));
                        } catch (Exception ignored) {
                            // 無効な日付は無視
                        }
                    }
                    if (municipalityCd != null && !municipalityCd.isBlank()) {
                        c.like(e.municipalityCd, "%" + municipalityCd + "%");
                    }
                    if (licenseStatus != null && !licenseStatus.isBlank()) {
                        c.like(e.licenseStatus, "%" + licenseStatus + "%");
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
