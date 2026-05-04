package io.github.taichi0373.kumamoto_henno_map.dto;

import lombok.Data;

/**
 * パスワードリセット実行リクエストDTO
 * <p>
 * パスワードリセットURLから新しいパスワードを設定する際のリクエストデータを保持する。
 * </p>
 */
@Data
public class PasswordResetConfirmRequestDto {

    /** リセットトークン */
    private String token;

    /** 新しいパスワード */
    private String newPassword;

    /** 新しいパスワード（確認用） */
    private String confirmNewPassword;

}
