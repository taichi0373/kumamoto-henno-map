package io.github.taichi0373.kumamoto_henno_map.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import io.github.taichi0373.kumamoto_henno_map.dto.LoginResponseDto;
import io.github.taichi0373.kumamoto_henno_map.repository.dao.UsersDao;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.UsersEntity;
import io.github.taichi0373.kumamoto_henno_map.constants.CodeConstants;
import io.github.taichi0373.kumamoto_henno_map.security.CustomUserDetails;
import io.github.taichi0373.kumamoto_henno_map.util.JwtUtil;

/**
 * 認証サービス
 * <p>
 * ログイン処理とSpring SecurityのUserDetailsService実装を担う。
 * </p>
 */
@Service
public class AuthService implements UserDetailsService {

    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    /** 最終ログインIPアドレスの最大長（LAST_LOGIN_IP カラム長） */
    private static final int LAST_LOGIN_IP_MAX_LENGTH = 45;

    /** ユーザー情報DAO */
    @Autowired
    private UsersDao usersDao;

    /** パスワードエンコーダー */
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /** JWTユーティリティ */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * ユーザー名によってユーザー詳細を取得する
     * @param username ユーザー名
     * @return ユーザー詳細
     * @throws UsernameNotFoundException ユーザーが見つからない場合
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsersEntity user = usersDao.selectByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("ユーザーが見つかりません: " + username);
        }
        return new CustomUserDetails(user);
    }

    /**
     * ユーザーIDによってユーザー詳細を取得する
     * @param userId ユーザーID
     * @return ユーザー詳細
     * @throws UsernameNotFoundException ユーザーが見つからない場合
     */
    public CustomUserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        UsersEntity user = usersDao.selectById(userId);
        if (user == null) {
            throw new UsernameNotFoundException("ユーザーが見つかりません: " + userId);
        }
        return new CustomUserDetails(user);
    }

    /**
     * アクセストークン生成結果レコード
     * <p>
     * トークンとユーザーエンティティをまとめて返すことでDB照会を1回に抑える。
     * </p>
     * @param token JWTアクセストークン
     * @param user  ユーザーエンティティ
     */
    public record AccessTokenResult(String token, UsersEntity user) {}

    /**
     * ユーザーIDからアクセストークンとユーザー情報を生成する（リフレッシュ時に使用）
     * @param userId ユーザーID
     * @return アクセストークンとユーザーエンティティ
     * @throws UsernameNotFoundException ユーザーが見つからない場合
     */
    public AccessTokenResult generateAccessTokenWithUser(Long userId) {
        UsersEntity user = usersDao.selectById(userId);
        if (user == null) {
            throw new UsernameNotFoundException("ユーザーが見つかりません: " + userId);
        }
        String token = jwtUtil.generateToken(new CustomUserDetails(user));
        return new AccessTokenResult(token, user);
    }

    /**
     * ユーザーIDからアクセストークンを生成する
     * @param userId ユーザーID
     * @return JWTアクセストークン
     */
    public String generateAccessToken(Long userId) {
        return generateAccessTokenWithUser(userId).token();
    }

    /**
     * ログイン処理
     * <p>
     * パスワードを検証し、成功時にJWTトークンを含むレスポンスを返す。
     * ログイン成功時は最終ログイン日時とIPアドレスをDBに保存する。
     * 保存は付随情報のため、失敗しても警告ログのみ出力してログインは継続する。
     * </p>
     * @param username ユーザー名
     * @param password パスワード
     * @param clientIp クライアントIPアドレス
     * @return ログインレスポンスDTO（認証失敗時はnull）
     */
    public LoginResponseDto login(String username, String password, String clientIp) {
        UsersEntity user = usersDao.selectByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            return null;
        }
        try {
            usersDao.updateLastLogin(user.getUserId(), LocalDateTime.now(), truncateIp(clientIp));
        } catch (RuntimeException e) {
            logger.warn("最終ログイン情報の保存に失敗しました: userId={}", user.getUserId(), e);
        }

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtUtil.generateToken(userDetails);

        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setAdmin(CodeConstants.UserType.ADMIN.equals(user.getIsAdmin()));
        return response;
    }

    /**
     * IPアドレスをカラム長以内に切り詰める
     * @param clientIp クライアントIPアドレス
     * @return 最大長以内のIPアドレス（nullの場合はnull）
     */
    private String truncateIp(String clientIp) {
        if (clientIp == null || clientIp.length() <= LAST_LOGIN_IP_MAX_LENGTH) {
            return clientIp;
        }
        return clientIp.substring(0, LAST_LOGIN_IP_MAX_LENGTH);
    }
}
