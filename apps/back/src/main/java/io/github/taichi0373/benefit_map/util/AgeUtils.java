package io.github.taichi0373.benefit_map.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AgeUtils {

    /**
     * 生年月日から年齢を計算する
     * @param birthDate 生年月日（LocalDate）
     * @return 年齢（int）
     */
    public static int calculateAge(LocalDate birthDate) {
        if (ValidateUtils.isNullOrEmpty(birthDate)) {
            throw new IllegalArgumentException("生年月日が指定されていません。");
        }
        LocalDate today = LocalDate.now();
        if (birthDate.isAfter(today)) {
            throw new IllegalArgumentException("生年月日が未来日です。");
        }
        return Period.between(birthDate, today).getYears();
    }

    /**
     * 生年月日から年齢を計算する
     * @param birthDateStr 生年月日（yyyy-MM-dd）
     * @return 年齢（int）
     */
    public static int calculateAge(String birthDateStr) {
        try {
            LocalDate birthDate = LocalDate.parse(birthDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            return calculateAge(birthDate);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("生年月日の形式が不正です。yyyy-MM-dd形式で入力してください。");
        }
    }

    /** コンストラクタ */
    private AgeUtils() {}
}
