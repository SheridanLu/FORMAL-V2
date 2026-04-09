package com.mochu.framework.security;

import com.mochu.common.constant.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * JWT 工具类 — 对照 V3.2 §4.1.2
 * Token 含 userId、角色列表、过期时间，有效期 30 天，剩余 7 天自动刷新
 */
@Component
public class JwtUtils {

    private final SecretKey key;
    private final long expireMillis;
    private final long refreshThresholdMillis;

    public JwtUtils(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expireMillis = Constants.TOKEN_EXPIRE_SECONDS * 1000;
        this.refreshThresholdMillis = Constants.TOKEN_REFRESH_THRESHOLD_SECONDS * 1000;
    }

    /**
     * 生成 JWT Token — V3.2 spec: payload 含 userId, roleList, exp, clientType
     */
    public String generateToken(Integer userId, String username, List<String> roles, String clientType) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expireMillis);
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("roles", roles)
                .claim("clientType", clientType)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    /**
     * 生成 JWT Token（兼容不传 clientType 的场景，默认 pc）
     */
    public String generateToken(Integer userId, String username, List<String> roles) {
        return generateToken(userId, username, roles, "pc");
    }

    /**
     * 解析 Token，返回 Claims
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从 Token 提取 userId
     */
    public Integer getUserId(String token) {
        return Integer.valueOf(parseToken(token).getSubject());
    }

    /**
     * 从 Token 提取 username
     */
    public String getUsername(String token) {
        return parseToken(token).get("username", String.class);
    }

    /**
     * 判断 Token 是否过期
     */
    public boolean isExpired(String token) {
        return parseToken(token).getExpiration().before(new Date());
    }

    /**
     * 判断是否应该刷新 Token（剩余有效期 < 7 天）
     */
    public boolean shouldRefresh(String token) {
        Date expiration = parseToken(token).getExpiration();
        long remaining = expiration.getTime() - System.currentTimeMillis();
        return remaining > 0 && remaining < refreshThresholdMillis;
    }
}
