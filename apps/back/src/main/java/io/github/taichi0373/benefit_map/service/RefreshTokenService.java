package io.github.taichi0373.benefit_map.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.taichi0373.benefit_map.repository.dao.RefreshTokensDao;
import io.github.taichi0373.benefit_map.repository.entity.RefreshTokensEntity;
import io.github.taichi0373.benefit_map.repository.entity.SystemField;

/**
 * リフレッシュトークンサービス
 * <p>
 * リフレッシュトークンの生成・検証・ローテーション・失効を行う。
 * DBにはトークン平文を保存せず、SHA-256ハッシュのみを保存する。
 * 使用のたびにローテーションして旧トークンを失効させることで再利用攻撃を防ぐ。
 * </p>
 */
@Service
public class RefreshTokenService {

    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);

    /** トークンのバイト長（256bit = 32バイト、hex文字列で64文字） */
    private static final int TOKEN_BYTE_LENGTH = 32;

    /** 乱数生成器（インスタンス共有で初期化コストを節約。SecureRandom はスレッドセーフ） */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /** リフレッシュトークン有効期限（秒） */
    @Value("${jwt.refresh-expiration:2592000}")
    private long refreshExpirationSeconds;

    /** リフレッシュトークンDAO */
    @Autowired
    private RefreshTokensDao refreshTokensDao;

    /**
     * リフレッシュトークンを生成してDBに登録する
     * @param userId ユーザーID
     * @return 平文トークン（Cookieに格納する値）
     */
    @Transactional
    public String createRefreshToken(Long userId) {
        String plainToken = generatePlainToken();
        LocalDateTime now = LocalDateTime.now();

        RefreshTokensEntity entity = new RefreshTokensEntity();
        entity.setUserId(userId);
        entity.setTokenHash(hashToken(plainToken));
        entity.setExpiresAt(now.plusSeconds(refreshExpirationSeconds));
        entity.setRevoked(false);
        entity.setSystemField(new SystemField(now, now));
        refreshTokensDao.insert(entity);

        return plainToken;
    }

    /**
     * リフレッシュトークンを検証してローテーションする
     * <p>
     * 「条件付きUPDATE → 件数チェック → 新トークン発行」の順で実行することで
     * 並行リクエスト（レース）に対して原子的なローテーションを保証する。
     * SELECT と UPDATE を分離すると間に割り込みが可能になるため、
     * 有効性の最終判定は必ず DB の条件付き UPDATE の更新件数で行う。
     * </p>
     * @param plainToken Cookieから取得した平文トークン
     * @return 検証成功時は新しい平文トークンとユーザーIDのペア、失敗時はnull
     */
    @Transactional
    public RotationResult validateAndRotate(String plainToken) {
        if (plainToken == null || plainToken.isBlank()) {
            return null;
        }

        String tokenHash = hashToken(plainToken);
        LocalDateTime now = LocalDateTime.now();

        // userId 取得のための SELECT（存在確認のみ。有効性の判定は条件付き UPDATE で行う）
        RefreshTokensEntity entity = refreshTokensDao.selectByTokenHash(tokenHash);
        if (entity == null) {
            logger.debug("リフレッシュトークンが存在しません");
            return null;
        }

        // 「revoked=false かつ expires_at > now」の条件付き UPDATE で原子的に失効させる。
        // 並行リクエストが同一トークンで到達しても、DB レベルで一方のみが1件更新される。
        // 更新件数 0 = 失効済み・期限切れ・または競合で負けた → 新トークンを発行しない。
        int updated = refreshTokensDao.revokeIfValidByTokenHash(tokenHash, now);
        if (updated == 0) {
            logger.warn("リフレッシュトークンのローテーション失敗（失効済み・期限切れ・競合）userId={}", entity.getUserId());
            return null;
        }

        // 更新件数 1 = 確実に自分だけが失効させた → 新トークンを発行する
        String newPlainToken = createRefreshToken(entity.getUserId());
        return new RotationResult(newPlainToken, entity.getUserId());
    }

    /**
     * リフレッシュトークンを失効させる（ログアウト時）
     * <p>
     * 条件付き UPDATE（revoked=false かつ expires_at > now）を使用する。
     * 更新件数0（失効済み・期限切れ）はログアウト時には正常系のため無視する。
     * </p>
     * @param plainToken Cookieから取得した平文トークン
     */
    @Transactional
    public void revoke(String plainToken) {
        if (plainToken == null || plainToken.isBlank()) {
            return;
        }
        refreshTokensDao.revokeIfValidByTokenHash(hashToken(plainToken), LocalDateTime.now());
    }

    /**
     * 期限切れ・失効済みトークンの定期クリーンアップ（毎日午前3時）
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanupExpiredTokens() {
        int deleted = refreshTokensDao.deleteExpiredOrRevoked(LocalDateTime.now());
        logger.info("リフレッシュトークンのクリーンアップ完了: {}件削除", deleted);
    }

    /**
     * ローテーション結果を保持するrecord
     * @param newPlainToken 新しい平文トークン
     * @param userId ユーザーID
     */
    public record RotationResult(String newPlainToken, Long userId) {}

    /**
     * SecureRandomで平文トークンを生成する（64文字hex文字列）
     * @return 平文トークン
     */
    private String generatePlainToken() {
        byte[] tokenBytes = new byte[TOKEN_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(tokenBytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : tokenBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * トークンをSHA-256でハッシュ化する（DBに保存するハッシュ値）
     * @param token 平文トークン
     * @return SHA-256ハッシュ（64文字hex文字列）
     */
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256アルゴリズムが利用できません", e);
        }
    }
}
