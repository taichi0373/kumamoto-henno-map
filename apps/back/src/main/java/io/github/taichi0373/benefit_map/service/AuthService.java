package io.github.taichi0373.benefit_map.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.taichi0373.benefit_map.dto.LoginResponseDto;
import io.github.taichi0373.benefit_map.repository.dao.UsersDao;
import io.github.taichi0373.benefit_map.repository.entity.UsersEntity;
import io.github.taichi0373.benefit_map.security.CustomUserDetails;
import io.github.taichi0373.benefit_map.util.JwtUtil;

/**
 * 認証サービス
 * <p>
 * ログイン処理とSpring SecurityのUserDetailsService実装を担う。
 * </p>
 */
@Service
public class AuthService implements UserDetailsService {

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
     * </p>
     * @param username ユーザー名
     * @param password パスワード
     * @return ログインレスポンスDTO（認証失敗時はnull）
     */
    public LoginResponseDto login(String username, String password) {
        UsersEntity user = usersDao.selectByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            return null;
        }
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtUtil.generateToken(userDetails);

        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setAdmin("1".equals(user.getIsAdmin()));
        return response;
    }
}
