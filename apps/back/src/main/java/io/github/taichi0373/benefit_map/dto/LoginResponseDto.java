package io.github.taichi0373.benefit_map.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * ログインレスポンスDTO
 */
@Data
public class LoginResponseDto {

    /** JWTトークン（HttpOnly Cookie に設定するため JSON レスポンスには含めない） */
    @JsonIgnore
    private String token;

    /** ユーザーID */
    private Long userId;

    /** ユーザー名 */
    private String username;
}
