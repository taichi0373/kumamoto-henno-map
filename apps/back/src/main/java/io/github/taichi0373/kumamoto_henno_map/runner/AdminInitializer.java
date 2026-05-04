package io.github.taichi0373.kumamoto_henno_map.runner;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import io.github.taichi0373.kumamoto_henno_map.repository.dao.UsersDao;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.SystemField;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.UsersEntity;

/**
 * アプリ起動時に管理者ユーザーを初期化するランナー。
 * <p>
 * 環境変数 {@code ADMIN_USERNAME} / {@code ADMIN_PASSWORD} / {@code ADMIN_EMAIL} が
 * すべて設定されており、かつ同名ユーザーが存在しない場合のみ管理者ユーザーを作成する。
 * </p>
 */
@Component
public class AdminInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    private final UsersDao usersDao;
    private final BCryptPasswordEncoder passwordEncoder;

    /** コンストラクタインジェクション */
    public AdminInitializer(UsersDao usersDao, BCryptPasswordEncoder passwordEncoder) {
        this.usersDao = usersDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String username = System.getenv("ADMIN_USERNAME");
        String password = System.getenv("ADMIN_PASSWORD");
        String email    = System.getenv("ADMIN_EMAIL");

        if (username == null || password == null || email == null) {
            log.info("管理者初期化スキップ: 環境変数 ADMIN_USERNAME / ADMIN_PASSWORD / ADMIN_EMAIL が未設定");
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
