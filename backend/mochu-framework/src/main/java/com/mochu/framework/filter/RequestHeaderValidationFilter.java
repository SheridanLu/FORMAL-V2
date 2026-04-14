package com.mochu.framework.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mochu.common.result.R;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Set;

/**
 * 请求头校验过滤器
 * P5 §8.2: X-Request-Id 和 X-Client-Type 必填
 * 写操作缺少 X-Idempotency-Key -> 400
 */
@Slf4j
public class RequestHeaderValidationFilter implements Filter {

    private static final Set<String> WRITE_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/api/v1/auth/", "/v3/api-docs", "/swagger-ui", "/doc.html");
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String uri = req.getRequestURI();

        // 公开接口跳过
        if (PUBLIC_PATHS.stream().anyMatch(uri::startsWith)) {
            chain.doFilter(request, response);
            return;
        }

        // 校验 X-Request-Id
        String requestId = req.getHeader("X-Request-Id");
        if (requestId == null || requestId.isBlank()) {
            writeError(resp, 400, "缺少请求头 X-Request-Id");
            return;
        }

        // 校验 X-Client-Type
        String clientType = req.getHeader("X-Client-Type");
        if (clientType == null || clientType.isBlank()) {
            writeError(resp, 400, "缺少请求头 X-Client-Type");
            return;
        }

        // 写操作校验 X-Idempotency-Key
        String method = req.getMethod().toUpperCase();
        if (WRITE_METHODS.contains(method)) {
            String idempotencyKey = req.getHeader("X-Idempotency-Key");
            if (idempotencyKey == null || idempotencyKey.isBlank()) {
                writeError(resp, 400, "写操作缺少请求头 X-Idempotency-Key");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private void writeError(HttpServletResponse resp, int code, String message)
            throws IOException {
        resp.setStatus(code);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(objectMapper.writeValueAsString(
                R.fail(code, message)));
    }
}
