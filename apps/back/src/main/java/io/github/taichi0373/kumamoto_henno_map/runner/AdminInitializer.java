package io.github.taichi0373.kumamoto_henno_map.runner;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import io.github.taichi0373.kumamoto_henno_map.repository.dao.UsersDao;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.SystemField;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.UsersEntity;

/**
 * アプリ起動時に管理者ユーザーを初期化するランナー。
 * <p>
 * プロパティ {@code admin.username} / {@code admin.password} / {@code admin.email} が
 * すべて設定されており、かつ同名ユーザーが存在しない場合のみ管理者ユーザーを作成する。
 * </p>
 */
@Component
public class AdminInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    private final UsersDao usersDao;
    private final BCryptPasswordEncoder passwordEncoder;

    /** 管理者ユーザー名 */
    @Value("${admin.username:}")
    private String username;

    /** 管理者パスワード */
    @Value("${admin.password:}")
    private String password;

    /** 管理者メールアドレス */
    @Value("${admin.email:}")
    private String email;

    /** コンストラクタインジェクション */
    public AdminInitializer(UsersDao usersDao, BCryptPasswordEncoder passwordEncoder) {
        this.usersDao = usersDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            log.info("管理者初期化スキップ: admin.username / admin.password / admin.email が未設定");
            return;
        }

        if (usersDao.selectByUsername(username) != null) {
            log.info("管理者初期化スキップ: ユーザー '{}' は既に存在します", username);
            return;
        }

        UsersEntity admin = new UsersEntity();
        admin.setUsername(username);
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setEmail(email);
        admin.setIsAdmin("1");
        LocalDateTime now = LocalDateTime.now();
        admin.setSystemField(new SystemField(now, now));

        usersDao.insert(admin);
        log.info("管理者ユーザー '{}' を作成しました", username);
    }
}
