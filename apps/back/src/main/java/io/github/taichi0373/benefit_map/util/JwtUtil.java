package io.github.taichi0373.benefit_map.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

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

    /** JWTシークレットキー */
    @Value("${jwt.secret}")
    private String secret;

    /** トークン有効期限（ミリ秒、デフォルト: 1時間） */
    @Value("${jwt.expiration:3600000}")
    private long expiration;

    /**
     * 署名キーを取得する
     * @return HMAC-SHA256 署名キー
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * JWTトークンを生成する
     * @param userDetails ユーザー詳細
     * @return JWTトークン文字列
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * トークンからユーザー名を取得する
     * @param token JWTトークン
     * @return ユーザー名
     */
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * トークンを検証する
     * @param token JWTトークン
     * @param userDetails ユーザー詳細
     * @return 有効な場合はtrue
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
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
