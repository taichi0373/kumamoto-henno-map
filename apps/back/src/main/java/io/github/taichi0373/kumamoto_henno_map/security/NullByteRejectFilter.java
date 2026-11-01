package io.github.taichi0373.kumamoto_henno_map.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.taichi0373.kumamoto_henno_map.dto.ApiResponseDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * NUL文字拒否フィルター
 * <p>
 * リクエストパラメータに NUL 文字（%00）が含まれる場合は 400 を返す。
 * PostgreSQL は文字列中の NUL 文字を扱えず、DB まで渡すと 500 になるため入口で拒否する。
 * </p>
 */
public class NullByteRejectFilter extends OncePerRequestFilter {

    /** NUL文字 */
    private static final char NULL_CHAR = '\0';

    /** エラーメッセージ */
    private static final String MESSAGE_INVALID_PARAMETER = "リクエストパラメータに不正な文字が含まれています";

    /** JSON変換 */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * リクエストパラメータを検証する
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (hasNullByte(request)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            objectMapper.writeValue(response.getOutputStream(), ApiResponseDto.error(MESSAGE_INVALID_PARAMETER));
            return;
        }
        filterChain.doFilter(request, response);
    }

    /**
     * リクエストパラメータに NUL 文字が含まれるか判定する
     *
     * @param request リクエスト
     * @return NUL 文字が含まれる場合は true
     */
    private boolean hasNullByte(HttpServletRequest request) {
        return request.getParameterMap().values().stream()
                .flatMap(java.util.Arrays::stream)
                .anyMatch(value -> value.indexOf(NULL_CHAR) >= 0);
    }
}
