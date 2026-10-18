package io.github.taichi0373.kumamoto_henno_map.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * アカウント削除リクエストDTO
 * <p>
 * ログイン済みユーザーが退会する際のリクエストデータを保持する。
 * 本人確認のためのパスワードを受け取る。
 * パスワードが toString でログ出力されるリスクを避けるため @Data は使用しない。
 * </p>
 */
@Getter
@Setter
public class DeleteAccountRequestDto {

    /** 本人確認用パスワード */
    private String password;

}
