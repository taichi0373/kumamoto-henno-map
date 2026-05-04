package io.github.taichi0373.kumamoto_henno_map.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理者セキュリティテスト用のスタブコントローラ
 * <p>
 * AdminSecurityTest でのみ使用するテスト専用コントローラ。
 * /admin/** エンドポイントに到達できることを検証するために使用する。
 * </p>
 */
@RestController
@RequestMapping("/admin")
class AdminTestController {

    /**
     * テスト用 GET /admin/benefits
     * @return ダミーレスポンス文字列
     */
    @GetMapping("/benefits")
    public String getBenefits() {
        return "ok";
    }
}
