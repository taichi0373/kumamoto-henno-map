package io.github.taichi0373.benefit_map.exception;

/**
 * ユーザー名重複例外
 * データベースの一意制約違反時にスローされる
 */
public class DuplicateUserException extends RuntimeException {
    
    /**
     * コンストラクタ
     * 
     * @param message エラーメッセージ
     */
    public DuplicateUserException(String message) {
        super(message);
    }
    
    /**
     * コンストラクタ
     * 
     * @param message エラーメッセージ
     * @param cause 原因となった例外
     */
    public DuplicateUserException(String message, Throwable cause) {
        super(message, cause);
    }
}