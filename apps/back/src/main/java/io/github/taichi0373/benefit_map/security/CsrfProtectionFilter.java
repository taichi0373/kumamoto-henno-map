package io.github.taichi0373.benefit_map.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.taichi0373.benefit_map.dto.ApiResponseDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CSRFカスタム対策フィルター
 * <p>
 * POST / PUT / PATCH / DELETE リクエストに対して以下の検証を行う。
 * <ol>
 *   <li>Origin ヘッダーが許可リスト（cors.allowed-origins）に含まれること</li>
 *   <li>X-Service-Name ヘッダーが期待値（app.security.expected-service-name）と一致すること</li>
 * </ol>
 * いずれかの検証に失敗した場合は HTTP 403 を返し、後続のフィルターチェーンは実行しない。
 * </p>
 * <p>
 * 以下のパスは検証をスキップする（認証前エンドポイントのため）。
 * <ul>
 *   <li>{@code /auth/**} — ログイン・ログアウト・パスワードリセット</li>
 *   <li>{@code POST /users/signup} — ユーザー登録</li>
 * </ul>
 * </p>
 */
public class CsrfProtectionFilter extends OncePerRequestFilter {

    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(CsrfProtectionFilter.class);

    /** 検証対象のHTTPメソッド */
    private static final Set<String> PROTECTED_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");

    /** カスタムヘッダー名 */
    private static final String SERVICE_NAME_HEADER = "X-Service-Name";

    /** 許可されたOriginリスト */
    private final List<String> allowedOrigins;

    /** 期待するサービス名 */
    private final String expectedServiceName;

    /** JSONシリアライザー */
    private final ObjectMapper objectMapper;

    /**
     * コンストラクタ
     *
     * @param allowedOrigins        パース済みの許可Originリスト（{@code CorsConfig.getParsedAllowedOrigins()} から取得）
     * @param expectedServiceName   期待するX-Service-Nameヘッダー値
     * @param objectMapper          JSONシリアライザー
     */
    public CsrfProtectionFilter(List<String> allowedOrigins, String expectedServiceName, ObjectMapper objectMapper) {
        this.allowedOrigins = allowedOrigins;
        this.expectedServiceName = expectedServiceName;
        this.objectMapper = objectMapper;
    }

    /**
     * 認証前エンドポイントはCSRF検証をスキップする
     * <p>
     * /auth/** はログイン・ログアウト・パスワードリセット等の認証前エンドポイント。
     * /users/signup はユーザー登録エンドポイント。
     * </p>
     *
     * @param request HTTPリクエスト
     * @return スキップする場合 true
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth/") || "/users/signup".equals(path);
    }

    /**
     * Origin ヘッダーと X-Service-Name ヘッダーを検証する
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String method = request.getMethod();

        // GET / HEAD / OPTIONS 等の読み取り系はスキップ
        if (!PROTECTED_METHODS.contains(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 検証1: Origin ヘッダー
        String origin = request.getHeader("Origin");
        if (origin == null) {
            logger.warn("CSRF検証失敗: Origin ヘッダーなし [{}] {}", method, request.getRequestURI());
            writeForbidden(response, "不正なリクエストです（Originヘッダーがありません）");
            return;
        }

        if (!allowedOrigins.contains(origin.trim())) {
            logger.warn("CSRF検証失敗: 許可されていないOrigin [{}] {} - origin={}", method, request.getRequestURI(), origin);
            writeForbidden(response, "不正なリクエストです（許可されていないOriginです）");
            return;
        }

        // 検証2: X-Service-Name カスタムヘッダー
        String serviceName = request.getHeader(SERVICE_NAME_HEADER);
        if (serviceName == null) {
            logger.warn("CSRF検証失敗: X-Service-Name ヘッダーなし [{}] {}", method, request.getRequestURI());
            writeForbidden(response, "不正なリクエストです（X-Service-Nameヘッダーがありません）");
            return;
        }

        if (!expectedServiceName.equals(serviceName.trim())) {
            logger.warn("CSRF検証失敗: X-Service-Name 不一致 [{}] {} - got={}, expected={}",
                    method, request.getRequestURI(), serviceName, expectedServiceName);
            writeForbidden(response, "不正なリクエストです（X-Service-Nameが正しくありません）");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 403 レスポンスをJSON形式で書き込む
     *
     * @param response HTTPレスポンス
     * @param message  エラーメッセージ
     * @throws IOException 書き込みエラー時
     */
    private void writeForbidden(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getOutputStream(), ApiResponseDto.error(message));
    }
}
