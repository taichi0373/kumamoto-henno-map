package io.github.taichi0373.benefit_map.security;

import java.io.IOException;

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
 * リクエストごとに Authorization ヘッダーから Bearer トークンを抽出し、
 * 検証後に SecurityContextHolder へ認証情報を設定する。
 * </p>
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

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

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

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
            // トークンが無効な場合は認証をスキップ（後続のセキュリティ設定でアクセス制御）
        }

        filterChain.doFilter(request, response);
    }
}
