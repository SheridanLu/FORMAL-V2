package com.mochu.common.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 企业微信消息发送 — 真实实现
 *
 * <p>通过企业微信 Server API 发送应用消息（文本卡片）
 * <p>当 message.wechat.enabled=true 时激活
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "message.wechat.enabled", havingValue = "true")
public class RealWechatSender implements WechatSender {

    @Value("${message.wechat.corp-id:}")
    private String corpId;

    @Value("${message.wechat.agent-id:}")
    private String agentId;

    @Value("${message.wechat.secret:}")
    private String secret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final StringRedisTemplate redisTemplate;

    private static final String TOKEN_KEY = "wechat:access_token";
    private static final String TOKEN_LOCK_KEY = "wechat:token:lock";
    private static final String TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s";
    private static final String SEND_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=%s";

    public RealWechatSender(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void sendTextCard(String userId, String title, String description, String url) {
        try {
            String token = getAccessToken();
            if (token == null) {
                log.error("[企业微信] 获取 access_token 失败, corpId={}", corpId);
                return;
            }

            Map<String, Object> body = Map.of(
                    "touser", userId,
                    "msgtype", "textcard",
                    "agentid", Integer.parseInt(agentId),
                    "textcard", Map.of(
                            "title", title,
                            "description", description != null ? description : "",
                            "url", url != null ? url : "",
                            "btntxt", "查看详情"
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(body), headers);

            String sendUrl = String.format(SEND_URL, token);
            ResponseEntity<String> resp = restTemplate.postForEntity(sendUrl, entity, String.class);

            if (resp.getStatusCode().is2xxSuccessful()) {
                log.info("[企业微信] 发送成功: userId={}, title={}", userId, title);
            } else {
                log.warn("[企业微信] 发送返回非200: {}", resp.getBody());
            }
        } catch (Exception e) {
            log.error("[企业微信] 发送失败: userId={}, title={}", userId, title, e);
        }
    }

    /**
     * 获取 access_token（带 Redis 缓存，有效期 7200s，提前 300s 刷新）
     * 使用 Redis SETNX 分布式锁防止多线程同时刷新导致 token stampede
     */
    private String getAccessToken() {
        String cached = redisTemplate.opsForValue().get(TOKEN_KEY);
        if (cached != null) return cached;

        // 尝试获取分布式锁
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(TOKEN_LOCK_KEY, "1", 10, TimeUnit.SECONDS);

        if (Boolean.TRUE.equals(locked)) {
            try {
                // 双重检查：获锁后再次检查缓存（可能其他线程已刷新）
                cached = redisTemplate.opsForValue().get(TOKEN_KEY);
                if (cached != null) return cached;

                String tokenUrl = String.format(TOKEN_URL, corpId, secret);
                ResponseEntity<Map> resp = restTemplate.getForEntity(tokenUrl, Map.class);
                if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                    Object errcode = resp.getBody().get("errcode");
                    if (errcode != null && ((Number) errcode).intValue() == 0) {
                        String token = (String) resp.getBody().get("access_token");
                        // 缓存 115 分钟（比实际 2h 少 5 分钟）
                        redisTemplate.opsForValue().set(TOKEN_KEY, token, Duration.ofMinutes(115));
                        return token;
                    } else {
                        log.error("[企业微信] 获取token失败: {}", resp.getBody());
                    }
                }
            } catch (Exception e) {
                log.error("[企业微信] 获取token异常", e);
            } finally {
                redisTemplate.delete(TOKEN_LOCK_KEY);
            }
        } else {
            // 未获取到锁，等待后重试读取缓存
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            cached = redisTemplate.opsForValue().get(TOKEN_KEY);
            if (cached != null) return cached;
            log.warn("[企业微信] 等待token刷新超时，缓存仍为空");
        }
        return null;
    }
}
