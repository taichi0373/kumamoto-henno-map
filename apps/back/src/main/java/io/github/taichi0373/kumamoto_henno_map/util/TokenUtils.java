package io.github.taichi0373.kumamoto_henno_map.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * ワンタイムトークンユーティリティ
 * <p>
 * パスワードリセット・メールアドレス確認で使用するトークンの生成とハッシュ化を提供する。
 * DBには平文トークンを保存せず、{@link #hashToken(String)} のハッシュ値のみを保存する。
 * </p>
 */
public final class TokenUtils {

    /** トークンのバイト長（256bit = 32バイト、hex文字列で64文字） */
    private static final int TOKEN_BYTE_LENGTH = 32;

    /** 乱数生成器（スレッドセーフ） */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * インスタンス化禁止
     */
    private TokenUtils() {
    }

    /**
     * SecureRandomで64文字のhexトークンを生成する
     * @return 平文トークン（64文字hex文字列）
     */
    public static String generateToken() {
        byte[] tokenBytes = new byte[TOKEN_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(tokenBytes);
        return toHex(tokenBytes);
    }

    /**
     * トークンをSHA-256でハッシュ化する
     * <p>
     * メールには平文トークンを送信し、照合時に同じハッシュを計算して比較する。
     * </p>
     * @param token 平文トークン
     * @return SHA-256ハッシュ（64文字hex文字列）
     */
    public static String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return toHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            // SHA-256はJava標準で必ず利用可能なため到達しない
            throw new IllegalStateException("SHA-256アルゴリズムが利用できません", e);
        }
    }

    /**
     * バイト配列をhex文字列に変換する
     * @param bytes バイト配列
     * @return hex文字列
     */
    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
