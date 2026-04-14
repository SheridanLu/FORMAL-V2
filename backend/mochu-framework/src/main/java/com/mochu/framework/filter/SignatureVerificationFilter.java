package com.mochu.framework.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mochu.common.result.R;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;

/**
 * HMAC-SHA256 签名验证过滤器
 * P5: 敏感操作需在请求头携带 X-Signature
 * 签名内容 = method + uri + timestamp + requestId
 */
@Slf4j
@Component
public class SignatureVerificationFilter implements Filter {

    @Value("${security.signature-key:MochuOA_Signature_Key_2026}")
    private String signatureKey;

    /** 需要签名验证的路径 */
    private static final Set<String> SIGNED_PATHS = Set.of(
            "/api/v1/auth/login-by-password",
            "/api/v1/auth/login-by-sms",
            "/api/v1/auth/forgot-password",
            "/api/v1/auth/reset-password",
            "/api/v1/contracts",
            "/api/v1/payment/apply",
            "/api/v1/admin/users/reset-password"
    );

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String method = req.getMethod().toUpperCase();

        // 仅校验匹配的路径 + POST/PUT/PATCH
        boolean needSign = SIGNED_PATHS.stream().anyMatch(uri::startsWith)
                && Set.of("POST", "PUT", "PATCH").contains(method);

        if (!needSign) {
            chain.doFilter(request, response);
            return;
        }

        String signature = req.getHeader("X-Signature");
        String timestamp = req.getHeader("X-Timestamp");
        String requestId = req.getHeader("X-Request-Id");

        if (signature == null || timestamp == null) {
            writeError(resp, 400, "敏感操作需要签名验证");
            return;
        }

        // 校验时间戳（5分钟内有效）
        try {
            long ts = Long.parseLong(timestamp);
            if (Math.abs(System.currentTimeMillis() - ts) > 5 * 60 * 1000) {
                writeError(resp, 400, "签名已过期");
                return;
            }
        } catch (NumberFormatException e) {
            writeError(resp, 400, "时间戳格式无效");
            return;
        }

        // 验证签名
        String payload = method + "|" + uri + "|" + timestamp + "|" + requestId;
        String expected = hmacSha256(payload, signatureKey);
        if (!expected.equals(signature)) {
            writeError(resp, 400, "签名验证失败");
            return;
        }

        chain.doFilter(request, response);
    }

    private String hmacSha256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("签名计算失败", e);
        }
    }

    private void writeError(HttpServletResponse resp, int code, String msg)
            throws IOException {
        resp.setStatus(code);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(objectMapper.writeValueAsString(R.fail(code, msg)));
    }
}
