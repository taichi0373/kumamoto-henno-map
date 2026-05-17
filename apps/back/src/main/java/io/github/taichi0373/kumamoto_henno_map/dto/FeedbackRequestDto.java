package io.github.taichi0373.kumamoto_henno_map.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * フィードバックリクエストDTO
 * <p>
 * ユーザーからのご意見・ご要望を受け取るリクエストデータ。
 * </p>
 */
@Schema(description = "フィードバックリクエスト")
@Data
public class FeedbackRequestDto {

    /** カテゴリ（BUG / REQUEST / OTHER） */
    @Schema(description = "フィードバックカテゴリ", example = "BUG",
            allowableValues = {"BUG", "REQUEST", "OTHER"})
    private String category;

    /** お名前（任意） */
    @Schema(description = "送信者の名前（任意）", example = "山田太郎")
    private String name;

    /** メールアドレス（任意） */
    @Schema(description = "送信者のメールアドレス（任意）", example = "taro@example.com")
    private String email;

    /** 内容（必須） */
    @Schema(description = "フィードバック内容（必須）", example = "地図が表示されません",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;
}
