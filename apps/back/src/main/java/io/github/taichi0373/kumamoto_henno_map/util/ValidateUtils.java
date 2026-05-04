package io.github.taichi0373.kumamoto_henno_map.util;

import java.util.Optional;
import java.util.regex.Pattern;
import org.springframework.util.ObjectUtils;

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

    /** コンストラクタ */
    private ValidateUtils() {}
}
