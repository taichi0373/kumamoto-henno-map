package io.github.taichi0373.kumamoto_henno_map.dto;

import lombok.Data;

/**
 * メールアドレス確認リクエストDTO
 * <p>
 * 確認メールのリンクに含まれるトークンでメールアドレスの確認を確定する際のリクエストデータを保持する。
 * </p>
 */
@Data
public class EmailVerificationConfirmRequestDto {

    /** 確認トークン */
    private String token;

}
