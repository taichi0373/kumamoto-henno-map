package io.github.taichi0373.benefit_map.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * ログインリクエストDTO
 */
@Schema(description = "ログインリクエスト")
@Data
public class LoginRequestDto {

    /** ユーザー名 */
    @Schema(description = "ユーザー名", example = "taro", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    /** パスワード */
    @Schema(description = "パスワード（平文。サーバー側でハッシュ化）", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
