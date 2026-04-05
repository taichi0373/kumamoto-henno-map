package io.github.taichi0373.benefit_map.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.security.core.userdetails.UserDetailsService;

import io.github.taichi0373.benefit_map.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT認証フィルター
 * <p>
 * リクエストごとに Authorization ヘッダー（Bearer トークン）からトークンを抽出し、
 * 検証後に SecurityContextHolder へ認証情報を設定する。
 * </p>
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    /** Authorization ヘッダー名 */
    private static final String AUTHORIZATION_HEADER = "Authorization";

    /** Bearer プレフィックス */
    private static final String BEARER_PREFIX = "Bearer ";

    /** JWTユーティリティ */
    private final JwtUtil jwtUtil;

    /** ユーザー詳細サービス */
    private final UserDetailsService userDetailsService;

    /**
     * コンストラクタ
     * @param jwtUtil JWTユーティリティ
     * @param userDetailsService ユーザー詳細サービス
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * JWT認証処理を実行する
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Authorization ヘッダーから Bearer トークンを抽出
        String token = extractTokenFromHeader(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String username = jwtUtil.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // トークンが無効・期限切れの場合は認証コンテキストをクリアして後続へ渡す
            SecurityContextHolder.clearContext();
            logger.debug("JWT検証失敗 [{}]: {} - {}", request.getRequestURI(),
                    e.getClass().getSimpleName(), e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * リクエストの Authorization ヘッダーから Bearer トークンを抽出する
     * @param request HTTPリクエスト
     * @return JWTトークン文字列、ヘッダーが存在しないまたは形式が不正の場合は null
     */
    private String extractTokenFromHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return authorizationHeader.substring(BEARER_PREFIX.length());
    }
}
