package io.github.taichi0373.benefit_map.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * ログインレスポンスDTO
 */
@Schema(description = "ログインレスポンス（JWT は HttpOnly Cookie に格納されるため JSON に含まれない）")
@Data
public class LoginResponseDto {

    /** JWTトークン（HttpOnly Cookie に設定するため JSON レスポンスには含めない） */
    @JsonIgnore
    @Schema(hidden = true)
    private String token;

    /** ユーザーID */
    @Schema(description = "ユーザーID", example = "1")
    private Long userId;

    /** ユーザー名 */
    @Schema(description = "ユーザー名", example = "taro")
    private String username;
}
