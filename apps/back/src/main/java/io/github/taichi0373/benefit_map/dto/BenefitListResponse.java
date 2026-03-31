package io.github.taichi0373.benefit_map.dto;

import java.util.List;

import io.github.taichi0373.benefit_map.repository.entity.BenefitDetailEntity;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 特典一覧レスポンス（OpenAPI ドキュメント用）
 * <p>
 * {@code ApiResponseDto<List<BenefitDetailEntity>>} のジェネリクス型を
 * OpenAPI スキーマとして明示するためのドキュメント専用クラス。
 * 実行時には使用しない。
 * </p>
 */
@Schema(description = "特典一覧 APIレスポンス")
public class BenefitListResponse {

    /** 処理成否 */
    @Schema(description = "処理成否。成功時: true、エラー時: false", example = "true")
    public boolean success;

    /** 特典一覧 */
    @ArraySchema(schema = @Schema(implementation = BenefitDetailEntity.class))
    public List<BenefitDetailEntity> data;

    /** エラーメッセージ（成功時は省略） */
    @Schema(description = "エラーメッセージ（失敗時のみ。成功時は省略）")
    public String message;
}
