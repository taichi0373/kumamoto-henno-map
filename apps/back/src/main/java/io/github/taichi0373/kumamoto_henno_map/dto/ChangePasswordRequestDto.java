package io.github.taichi0373.kumamoto_henno_map.dto;

import lombok.Data;

/**
 * パスワード変更リクエストDTO
 * <p>
 * ログイン済みユーザーがパスワードを変更する際のリクエストデータを保持する。
 * </p>
 */
@Data
public class ChangePasswordRequestDto {

    /** 現在のパスワード */
    private String currentPassword;

    /** 新しいパスワード */
    private String newPassword;

    /** 新しいパスワード（確認用） */
    private String confirmNewPassword;

}
