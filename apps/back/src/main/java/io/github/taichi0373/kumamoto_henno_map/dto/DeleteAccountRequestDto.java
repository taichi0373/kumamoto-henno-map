package io.github.taichi0373.kumamoto_henno_map.dto;

import lombok.Data;

/**
 * アカウント削除リクエストDTO
 * <p>
 * ログイン済みユーザーが退会する際のリクエストデータを保持する。
 * 本人確認のためのパスワードを受け取る。
 * </p>
 */
@Data
public class DeleteAccountRequestDto {

    /** 本人確認用パスワード */
    private String password;

}
