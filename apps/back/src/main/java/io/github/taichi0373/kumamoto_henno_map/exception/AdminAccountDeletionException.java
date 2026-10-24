package io.github.taichi0373.kumamoto_henno_map.exception;

/**
 * 管理者アカウント削除禁止例外
 * 管理者アカウントが自己削除（退会）を試みた場合にスローされる
 */
public class AdminAccountDeletionException extends RuntimeException {

    /**
     * コンストラクタ
     *
     * @param message エラーメッセージ
     */
    public AdminAccountDeletionException(String message) {
        super(message);
    }
}
