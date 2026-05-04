package io.github.taichi0373.benefit_map.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import lombok.NonNull;
import org.springframework.util.ObjectUtils;

import io.github.taichi0373.benefit_map.util.ValidateUtils;

/**
 * 型変換ユーティリティクラス
 */
public class TypeConvertUtils {

    /**
     * 空文字
     */
    private static final String STRING_EMPTY = "";

    /**
     * 第一引数が{@code null}の場合は空文字を、それ以外は第一引数を返す
     * @param s 文字列
     * @return 第一引数が{@code null}の場合は空文字を、それ以外は第一引数
     */
    @NonNull
    public static String toStringNullToEmpty(String s) {
        return Objects.toString(s, STRING_EMPTY);
    }

    /**
     * 第一引数が{@code null}の場合は空文字を、それ以外は第一引数の{@code toString}を呼び出した結果を返す
     * @param i 数値
     * @return 第一引数が{@code null}の場合は空文字を、それ以外は第一引数の{@code toString}を呼び出した結果
     */
    @NonNull
    public static String toStringNullToEmpty(Integer i) {
        return Objects.toString(i, STRING_EMPTY);
    }

    /**
     * 第一引数が{@code null}の場合は空文字を、それ以外は第一引数の{@code toString}を呼び出した結果を返す
     * @param l 数値
     * @return 第一引数が{@code null}の場合は空文字を、それ以外は第一引数の{@code toString}を呼び出した結果
     */
    @NonNull
    public static String toStringNullToEmpty(Long l) {
        return Objects.toString(l, STRING_EMPTY);
    }

    /**
     * 第一引数が{@code null}または{@code 0}の場合は空文字を、それ以外は第一引数を返す
     * @param s 文字列
     * @return 第一引数が{@code null}または{@code 0}の場合は空文字を、それ以外は第一引数
     */
    @NonNull
    public static String toStringNullOrZeroToEmpty(String s) {
        return s == null || Objects.equals(s, "0") ? STRING_EMPTY : s;
    }

    /**
     * 第一引数が{@code null}または{@code 0}の場合は空文字を、それ以外は第一引数を返す
     */
    @NonNull
    public static String toStringNullOrZeroToEmpty(Integer i) {
        return i == null || Objects.equals(i, 0) ? STRING_EMPTY : i.toString();
    }

    /**
     * 第一引数が{@code null}または{@code 0}の場合は空文字を、それ以外は第一引数を返す
     */
    @NonNull
    public static String toStringNullOrZeroToEmpty(Long l) {
        return l == null || Objects.equals(l, 0L) ? STRING_EMPTY : l.toString();
    }

    /**
     * 西暦日付8桁からLocalDateのインスタンスを生成する
     * @param seireki 8桁の西暦日付
     * @return LocalDateのインスタンス
     */
    public static LocalDate toLocalDate(int seireki) {
        return toLocalDate(String.valueOf(seireki));
    }

    /**
     * 西暦日付8桁からLocalDateのインスタンスを生成する
     * @param seireki 8桁の西暦日付
     * @return LocalDateのインスタンス
     */
    public static LocalDate toLocalDate(@NonNull String seireki) {
        // 西暦日付の桁数
        final int seirekiDigitForDate = 8;

        if (ValidateUtils.isNullOrEmpty(seireki)) {
            throw new IllegalArgumentException("西暦日付が指定されていません。");
        }
        if (seireki.length() != seirekiDigitForDate) {
            throw new IllegalArgumentException("西暦日付の桁数が不正です。");
        }
        if (!ValidateUtils.isHalfWidthNumber(seireki)) {
            throw new IllegalArgumentException("西暦日付の値が不正です。");
        }
        // 年月日を抽出
        int year = Integer.parseInt(seireki.substring(0, 4));
        int month = Integer.parseInt(seireki.substring(4, 6));
        int day = Integer.parseInt(seireki.substring(6, 8));

        // LocalDateの生成（不正な日付はDateTimeExceptionがスローされる）
        return LocalDate.of(year, month, day);

    }

    /** コンストラクタ */
    private TypeConvertUtils() {}
}
