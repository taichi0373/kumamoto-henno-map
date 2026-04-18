package io.github.taichi0373.benefit_map.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * ログインレスポンスDTO
 */
@Schema(description = "ログインレスポンス")
@Data
public class LoginResponseDto {

    /** JWTトークン */
    @Schema(description = "JWTトークン（Authorization: Bearer <token> で使用）")
    private String token;

    /** ユーザーID */
    @Schema(description = "ユーザーID", example = "1")
    private Long userId;

    /** ユーザー名 */
    @Schema(description = "ユーザー名", example = "taro")
    private String username;

    /** 管理者フラグ */
    @Schema(description = "管理者フラグ（trueの場合は管理者ユーザー）", example = "false")
    private boolean isAdmin;
}
