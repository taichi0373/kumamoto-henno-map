package io.github.taichi0373.kumamoto_henno_map.dto;

import lombok.Data;

/**
 * パスワードリセット要求リクエストDTO
 * <p>
 * パスワードリセットメールの送信を要求する際のリクエストデータを保持する。
 * </p>
 */
@Data
public class PasswordResetRequestDto {

    /** メールアドレス */
    private String email;

}
