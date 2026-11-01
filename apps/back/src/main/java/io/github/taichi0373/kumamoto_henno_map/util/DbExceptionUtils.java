package io.github.taichi0373.kumamoto_henno_map.util;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.github.taichi0373.kumamoto_henno_map.dto.ApiResponseDto;

/**
 * DB例外のレスポンス変換ユーティリティクラス
 * <p>
 * SQLState から入力起因のエラーを判定し、ステータスコードと固定メッセージに変換する。
 * レスポンスには例外の内容（SQL文やテーブル名など）を含めない。
 * </p>
 */
public class DbExceptionUtils {

    /** ロガー */
    private static final Logger log = LoggerFactory.getLogger(DbExceptionUtils.class);

    /** 一意制約違反の SQLState */
    private static final String SQL_STATE_UNIQUE_VIOLATION = "23505";
    /** データ例外（文字数超過・不正な文字など）の SQLState クラス */
    private static final String SQL_STATE_CLASS_DATA_EXCEPTION = "22";
    /** 整合性制約違反（必須・外部キーなど）の SQLState クラス */
    private static final String SQL_STATE_CLASS_INTEGRITY_VIOLATION = "23";

    /** 一意制約違反時のメッセージ */
    private static final String MESSAGE_DUPLICATE = "既に登録されています";
    /** 入力起因のエラー時のメッセージ */
    private static final String MESSAGE_INVALID_INPUT = "入力内容が正しくありません（必須項目の未入力、文字数の超過、存在しない参照先など）";

    /**
     * DB例外をエラーレスポンスに変換する
     *
     * @param <T>             レスポンスデータの型
     * @param e               DB例外
     * @param fallbackMessage 入力起因ではない場合のメッセージ
     * @return エラーレスポンス
     */
    public static <T> ResponseEntity<ApiResponseDto<T>> toErrorResponse(Exception e, String fallbackMessage) {
        String sqlState = findSqlState(e);
        if (SQL_STATE_UNIQUE_VIOLATION.equals(sqlState)) {
            log.warn("DB一意制約違反: SQLState={}", sqlState);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponseDto.error(MESSAGE_DUPLICATE));
        }
        if (sqlState != null && (sqlState.startsWith(SQL_STATE_CLASS_DATA_EXCEPTION)
                || sqlState.startsWith(SQL_STATE_CLASS_INTEGRITY_VIOLATION))) {
            log.warn("DB入力エラー: SQLState={}", sqlState);
            return ResponseEntity.badRequest().body(ApiResponseDto.error(MESSAGE_INVALID_INPUT));
        }
        log.error("DBエラー", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponseDto.error(fallbackMessage));
    }

    /**
     * 例外の原因をたどって SQLState を取得する
     *
     * @param e 例外
     * @return SQLState。見つからない場合は null
     */
    private static String findSqlState(Throwable e) {
        for (Throwable cause = e; cause != null; cause = cause.getCause()) {
            if (cause instanceof SQLException sqlException && sqlException.getSQLState() != null) {
                return sqlException.getSQLState();
            }
            if (cause.getCause() == cause) {
                break;
            }
        }
        return null;
    }

    /** コンストラクタ */
    private DbExceptionUtils() {}
}
