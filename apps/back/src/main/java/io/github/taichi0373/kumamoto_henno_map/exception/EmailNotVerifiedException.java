package io.github.taichi0373.kumamoto_henno_map.exception;

/**
 * メールアドレス未確認例外
 * パスワードは正しいが、メールアドレスの確認が完了していないユーザーのログイン時にスローされる
 */
public class EmailNotVerifiedException extends RuntimeException {

    /**
     * コンストラクタ
     *
     * @param message エラーメッセージ
     */
    public EmailNotVerifiedException(String message) {
        super(message);
    }
}
