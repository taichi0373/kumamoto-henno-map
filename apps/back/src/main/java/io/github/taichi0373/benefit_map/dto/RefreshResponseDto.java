package io.github.taichi0373.benefit_map.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * トークンリフレッシュレスポンスDTO
 */
@Schema(description = "トークンリフレッシュレスポンス")
@Data
@AllArgsConstructor
public class RefreshResponseDto {

    /** 新しいJWTアクセストークン */
    @Schema(description = "新しいJWTアクセストークン（Authorization: Bearer <token> で使用）")
    private String token;
}
