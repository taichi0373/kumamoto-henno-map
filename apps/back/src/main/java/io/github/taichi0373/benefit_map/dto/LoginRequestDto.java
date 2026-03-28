package io.github.taichi0373.benefit_map.dto;

import lombok.Data;

/**
 * ログインリクエストDTO
 */
@Data
public class LoginRequestDto {

    /** ユーザー名 */
    private String username;

    /** パスワード */
    private String password;
}
