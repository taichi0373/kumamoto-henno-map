package io.github.taichi0373.benefit_map.dto;

import lombok.Data;

/**
 * ログインレスポンスDTO
 */
@Data
public class LoginResponseDto {

    /** JWTトークン */
    private String token;

    /** ユーザーID */
    private Long userId;

    /** ユーザー名 */
    private String username;
}
