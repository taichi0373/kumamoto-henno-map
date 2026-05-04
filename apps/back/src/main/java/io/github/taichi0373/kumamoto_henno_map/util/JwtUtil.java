package io.github.taichi0373.kumamoto_henno_map.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import io.github.taichi0373.kumamoto_henno_map.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * JWTトークンのユーティリティクラス
 * <p>
 * トークンの生成・解析・検証を行う。
 * シークレットキーは application.properties の jwt.secret から取得する。
 * </p>
 */
@Component
public class JwtUtil {

    /** HS256 に必要な最低キー長（バイト） */
    private static final int MIN_SECRET_BYTES = 32;

    /** JWTシークレットキー */
    @Value("${jwt.secret}")
    private String secret;

    /** トークン有効期限（ミリ秒、デフォルト: 1時間） */
    @Value("${jwt.expiration:3600000}")
    private long expiration;

    /**
     * 起動時に jwt.secret の最低長を検証する。
     * HS256 は 256 bit（32 バイト）以上のキーが必要。
     * 要件を満たさない場合はアプリを起動失敗させる。
     */
    @PostConstruct
    void validateSecret() {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                    "jwt.secret は UTF-8 エンコード後に %d バイト以上必要です（現在: %d バイト）。"
                    .formatted(
                            MIN_SECRET_BYTES,
                            secret == null ? 0 : secret.getBytes(StandardCharsets.UTF_8).length
                    )
            );
        }
    }

    /**
     * 署名キーを取得する
     * @return HMAC-SHA256 署名キー
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * JWTトークンを生成する
     * @param userDetails カスタムユーザー詳細
     * @return JWTトークン文字列
     */
    public String generateToken(CustomUserDetails userDetails) {
        return Jwts.builder()
                .subject(String.valueOf(userDetails.getUserId()))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * トークンからユーザーIDを取得する
     * @param token JWTトークン
     * @return ユーザーID
     */
    public Long extractUserId(String token) {
        return Long.parseLong(getClaims(token).getSubject());
    }

    /**
     * トークンを検証する
     * @param token JWTトークン
     * @param userDetails カスタムユーザー詳細
     * @return 有効な場合はtrue
     */
    public boolean validateToken(String token, CustomUserDetails userDetails) {
        try {
            Long userId = extractUserId(token);
            return userId.equals(userDetails.getUserId()) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * トークンからClaimsを取得する
     * @param token JWTトークン
     * @return Claims
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * トークンが期限切れかどうかを確認する
     * @param token JWTトークン
     * @return 期限切れの場合はtrue
     */
    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }
}
