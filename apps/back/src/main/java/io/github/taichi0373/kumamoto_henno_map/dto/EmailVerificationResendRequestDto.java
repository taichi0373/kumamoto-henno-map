package io.github.taichi0373.kumamoto_henno_map.dto;

import lombok.Data;

/**
 * 確認メール再送リクエストDTO
 * <p>
 * 新規登録時の確認メールの再送を要求する際のリクエストデータを保持する。
 * </p>
 */
@Data
public class EmailVerificationResendRequestDto {

    /** メールアドレス */
    private String email;

}
