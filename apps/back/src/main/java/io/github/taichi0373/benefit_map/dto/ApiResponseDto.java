package io.github.taichi0373.benefit_map.dto;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 汎用APIレスポンスDTO
 * <p>
 * 全コントローラーで共通使用するレスポンス形式。
 * 成功時は {@code success: true, data: {...}} 、
 * 失敗時は {@code success: false, data: null, message: "..."} を返す。
 * {@code data} フィールドは成功・失敗を問わず常に出力される。
 * </p>
 *
 * @param <T> レスポンスデータの型
 */
@Schema(description = "共通APIレスポンス形式")
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDto<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 処理成否 */
    @Schema(description = "処理成否。成功時: true、エラー時: false", example = "true")
    private final boolean success;

    /** レスポンスデータ */
    @Schema(description = "レスポンスデータ（データなしの場合は null）")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private final T data;

    /** エラーメッセージ（失敗時のみ。成功時は省略） */
    @Schema(description = "エラーメッセージ（失敗時のみ。成功時は JSON に含まれない）", example = "ユーザー名またはパスワードが正しくありません")
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
