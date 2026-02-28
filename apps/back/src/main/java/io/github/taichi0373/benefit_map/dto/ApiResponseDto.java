package io.github.taichi0373.benefit_map.dto;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

/**
 * 汎用APIレスポンスDTO
 * <p>
 * 全コントローラーで共通使用するレスポンス形式。
 * 成功時は {@code success: true, data: {...}} 、
 * 失敗時は {@code success: false, message: "..."} を返す。
 * </p>
 *
 * @param <T> レスポンスデータの型
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDto<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 処理成否 */
    private final boolean success;

    /** レスポンスデータ（成功時のみ） */
    private final T data;

    /** エラーメッセージ（失敗時のみ） */
    private final String message;

    private ApiResponseDto(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    /**
     * 成功レスポンスを生成する
     *
     * @param <T>  レスポンスデータの型
     * @param data レスポンスデータ
     * @return 成功レスポンスDTO
     */
    public static <T> ApiResponseDto<T> success(T data) {
        return new ApiResponseDto<>(true, data, null);
    }

    /**
     * 失敗レスポンスを生成する
     *
     * @param <T>     レスポンスデータの型
     * @param message エラーメッセージ
     * @return 失敗レスポンスDTO
     */
    public static <T> ApiResponseDto<T> error(String message) {
        return new ApiResponseDto<>(false, null, message);
    }
}
