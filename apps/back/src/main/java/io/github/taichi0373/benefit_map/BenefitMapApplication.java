package io.github.taichi0373.benefit_map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 熊本県自主返納特典マップアプリケーション
 * <p>
 * Spring Boot アプリケーションのエントリーポイント。
 * </p>
 */
@SpringBootApplication
@EnableScheduling
public class BenefitMapApplication {
    public static void main(String[] args) {
        SpringApplication.run(BenefitMapApplication.class, args);
    }
}

