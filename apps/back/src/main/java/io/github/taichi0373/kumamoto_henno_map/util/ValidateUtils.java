package io.github.taichi0373.kumamoto_henno_map.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Pattern;
import org.springframework.util.ObjectUtils;

import io.github.taichi0373.kumamoto_henno_map.dto.RouteRequestDto;

/**
 * バリデーションユーティリティクラス
 */
public class ValidateUtils {

    /** 半角文字のパターン */
    private static final Pattern HALF_WIDTH_PATTERN = Pattern.compile("^[\\u0020-\\u007E]+$");
    /** 全角文字のパターン */
    private static final Pattern FULL_WIDTH_PATTERN = Pattern.compile("^[^ -~｡-ﾟ]+$");
    /** 半角数字のパターン */
    private static final Pattern HALF_WIDTH_NUMBER_PATTERN = Pattern.compile("^[0-9]+$");
    /** 全角数字のパターン */
    private static final Pattern FULL_WIDTH_NUMBER_PATTERN = Pattern.compile("^[０-９]+$");
    /** 半角アルファベットのパターン */
    private static final Pattern HALF_WIDTH_ALPHABET_PATTERN = Pattern.compile("^[A-Za-z]+$");
    /** 全角アルファベットのパターン */
    private static final Pattern FULL_WIDTH_ALPHABET_PATTERN = Pattern.compile("^[Ａ-Ｚａ-ｚ]+$");
    /** 電話番号のパターン */
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^(0\\d{1,4}-\\d{1,4}-\\d{4}|0\\d{9,10})$");
    /** メールアドレスのパターン */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");

    /** OTP交通手段のパターン（カンマ区切りの英大文字・アンダースコア） */
    private static final Pattern TRANSPORT_MODE_PATTERN = Pattern.compile("^[A-Z_]+(,[A-Z_]+)*$");
    /** 時刻のパターン（HH:mm または HH:mm:ss） */
    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]\\d|2[0-3]):[0-5]\\d(:[0-5]\\d)?$");

    /** 交通手段の最大文字数 */
    private static final int TRANSPORT_MODE_MAX_LENGTH = 50;
    /** 地点名称の最大文字数 */
    private static final int LOCATION_MAX_LENGTH = 200;
    /** 緯度の絶対値の上限 */
    private static final double LATITUDE_LIMIT = 90.0;
    /** 経度の絶対値の上限 */
    private static final double LONGITUDE_LIMIT = 180.0;

    /** パスワード最低文字数 */
    public static final int PASSWORD_MIN_LENGTH = 8;
    /** パスワード最大文字数 */
    public static final int PASSWORD_MAX_LENGTH = 64;
    /** ユーザー名最小文字数 */
    public static final int USERNAME_MIN_LENGTH = 3;
    /** ユーザー名最大文字数 */
    public static final int USERNAME_MAX_LENGTH = 30;
    /** ユーザー名のパターン（半角英数字・ハイフン・アンダースコアのみ） */
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9\\-_]+$");

    /**
     * オブジェクトが null または空文字かどうか
     */
    public static boolean isNullOrEmpty(Object obj) {
        return ObjectUtils.isEmpty(obj);
    }

    /**
     * 半角文字のみ
     */
    public static boolean isHalfWidthChar(String str) {
        if (isNullOrEmpty(str)) return false;
        return HALF_WIDTH_PATTERN.matcher(str).matches();
    }

    /**
     * 全角文字のみ
     */
    public static boolean isFullWidthChar(String str) {
        if (isNullOrEmpty(str)) return false;
        return FULL_WIDTH_PATTERN.matcher(str).matches();
    }

    /**
     * 半角数字のみ
     */
    public static boolean isHalfWidthNumber(String str) {
        if (isNullOrEmpty(str)) return false;
        return HALF_WIDTH_NUMBER_PATTERN.matcher(str).matches();
    }

    /**
     * 全角数字のみ
     */
    public static boolean isFullWidthNumber(String str) {
        if (isNullOrEmpty(str)) return false;
        return FULL_WIDTH_NUMBER_PATTERN.matcher(str).matches();
    }

    /**
     * 半角アルファベットのみ
     */
    public static boolean isHalfWidthAlphabet(String str) {
        if (isNullOrEmpty(str)) return false;
        return HALF_WIDTH_ALPHABET_PATTERN.matcher(str).matches();
    }

    /**
     * 全角アルファベットのみ
     */
    public static boolean isFullWidthAlphabet(String str) {
        if (isNullOrEmpty(str)) return false;
        return FULL_WIDTH_ALPHABET_PATTERN.matcher(str).matches();
    }

    /**
     * 電話番号形式
     * 例: 03-1234-5678, 09012345678
     */
    public static boolean isPhoneNumber(String str) {
        if (isNullOrEmpty(str)) return false;
        return PHONE_NUMBER_PATTERN.matcher(str).matches();
    }

    /**
     * メールアドレス形式
     */
    public static boolean isEmail(String str) {
        if (isNullOrEmpty(str)) return false;
        return EMAIL_PATTERN.matcher(str).matches();
    }

    /**
     * パスワードポリシーを満たすか（{@value #PASSWORD_MIN_LENGTH}〜{@value #PASSWORD_MAX_LENGTH}文字、空白のみ不可）
     */
    public static boolean isValidPassword(String password) {
        if (isNullOrEmpty(password)) return false;
        if (password.strip().isEmpty()) return false;
        return password.length() >= PASSWORD_MIN_LENGTH && password.length() <= PASSWORD_MAX_LENGTH;
    }

    /**
     * ユーザー名の形式チェック（半角英数字・ハイフン・アンダースコアのみ）
     */
    public static boolean isValidUsernameFormat(String username) {
        if (isNullOrEmpty(username)) return false;
        return USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * ユーザー名の一括バリデーション（必須・文字数・文字種）
     *
     * @param username 検証するユーザー名
     * @return エラーメッセージ。{@link Optional#empty()} の場合は検証通過
     */
    public static Optional<String> validateUsername(String username) {
        if (isNullOrEmpty(username)) {
            return Optional.of("ユーザー名を入力してください");
        }
        if (username.length() < USERNAME_MIN_LENGTH) {
            return Optional.of("ユーザー名は" + USERNAME_MIN_LENGTH + "文字以上で入力してください");
        }
        if (username.length() > USERNAME_MAX_LENGTH) {
            return Optional.of("ユーザー名は" + USERNAME_MAX_LENGTH + "文字以内で入力してください");
        }
        if (!isValidUsernameFormat(username)) {
            return Optional.of("ユーザー名は半角英数字・ハイフン・アンダースコアのみ使用できます");
        }
        return Optional.empty();
    }

    /**
     * 経路探索リクエストの一括バリデーション
     *
     * @param request 経路探索リクエスト
     * @return エラーメッセージ。{@link Optional#empty()} の場合は検証通過
     */
    public static Optional<String> validateRouteRequest(RouteRequestDto request) {
        String transportMode = request.getTransportMode();
        if (transportMode == null || transportMode.length() > TRANSPORT_MODE_MAX_LENGTH
                || !TRANSPORT_MODE_PATTERN.matcher(transportMode).matches()) {
            return Optional.of("交通手段が正しくありません");
        }
        if (!isInRange(request.getStartLat(), LATITUDE_LIMIT) || !isInRange(request.getEndLat(), LATITUDE_LIMIT)
                || !isInRange(request.getStartLon(), LONGITUDE_LIMIT) || !isInRange(request.getEndLon(), LONGITUDE_LIMIT)) {
            return Optional.of("出発地または目的地の座標が正しくありません");
        }
        if (!isValidDateOrEmpty(request.getDate())) {
            return Optional.of("日付が正しくありません");
        }
        if (!isNullOrEmpty(request.getTime()) && !TIME_PATTERN.matcher(request.getTime()).matches()) {
            return Optional.of("時刻が正しくありません");
        }
        if (isTooLong(request.getStartLocation()) || isTooLong(request.getEndLocation())) {
            return Optional.of("出発地または目的地の名称が長すぎます");
        }
        return Optional.empty();
    }

    /**
     * 値が -limit 以上 limit 以下か判定する（null・NaN は false）
     *
     * @param value 検証する値
     * @param limit 絶対値の上限
     * @return 範囲内の場合は true
     */
    private static boolean isInRange(Double value, double limit) {
        return value != null && value >= -limit && value <= limit;
    }

    /**
     * 値が空または yyyy-MM-dd 形式の実在する日付か判定する
     *
     * @param date 検証する日付文字列
     * @return 空または正しい日付の場合は true
     */
    private static boolean isValidDateOrEmpty(String date) {
        if (isNullOrEmpty(date)) {
            return true;
        }
        try {
            LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * 地点名称が上限文字数を超えるか判定する
     *
     * @param location 地点名称
     * @return 上限を超える場合は true
     */
    private static boolean isTooLong(String location) {
        return location != null && location.length() > LOCATION_MAX_LENGTH;
    }

    /** コンストラクタ */
    private ValidateUtils() {}
}
