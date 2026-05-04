package io.github.taichi0373.kumamoto_henno_map.dto;

import java.util.List;

import io.github.taichi0373.kumamoto_henno_map.repository.entity.MunicipalityEntity;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 市区町村一覧レスポンス（OpenAPI ドキュメント用）
 * <p>
 * {@code ApiResponseDto<List<MunicipalityEntity>>} のジェネリクス型を
 * OpenAPI スキーマとして明示するためのドキュメント専用クラス。
 * 実行時には使用しない。
 * </p>
 */
@Schema(description = "市区町村一覧 APIレスポンス")
public class MunicipalityListResponse {

    /** 処理成否 */
    @Schema(description = "処理成否。成功時: true、エラー時: false", example = "true")
    public boolean success;

    /** 市区町村一覧 */
    @ArraySchema(schema = @Schema(implementation = MunicipalityEntity.class))
    public List<MunicipalityEntity> data;

    /** エラーメッセージ（成功時は省略） */
    @Schema(description = "エラーメッセージ（失敗時のみ。成功時は省略）")
    public String message;
}
