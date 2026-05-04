package io.github.taichi0373.benefit_map.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ヘルスチェックコントローラー
 * Renderのスリープ対策として、GASから定期的にアクセスするためのエンドポイントを提供する。
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    /**
     * ヘルスチェック
     * @return 200 OK
     */
    @GetMapping
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
