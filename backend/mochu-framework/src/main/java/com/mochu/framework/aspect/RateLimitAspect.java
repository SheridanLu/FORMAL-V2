package com.mochu.framework.aspect;

import com.mochu.common.exception.BusinessException;
import com.mochu.common.security.SecurityUtils;
import com.mochu.framework.annotation.RateLimit;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitAspect {

    private final StringRedisTemplate redisTemplate;

    /**
     * #4 fix: Lua 脚本保证 INCR + EXPIRE 原子性，
     * 防止进程崩溃导致 key 无 TTL 永久阻塞用户
     */
    private static final String RATE_LIMIT_LUA =
            "local c = redis.call('incr', KEYS[1]); " +
            "if c == 1 then redis.call('expire', KEYS[1], ARGV[1]) end; " +
            "return c";
    private static final DefaultRedisScript<Long> RATE_LIMIT_SCRIPT =
            new DefaultRedisScript<>(RATE_LIMIT_LUA, Long.class);

    @Around("@annotation(com.mochu.framework.annotation.RateLimit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        String key = buildKey(rateLimit, method);
        String redisKey = "rate_limit:" + key;

        Long count = redisTemplate.execute(RATE_LIMIT_SCRIPT,
                List.of(redisKey), String.valueOf(rateLimit.period()));

        if (count != null && count > rateLimit.limit()) {
            log.warn("限流拦截: key={}, count={}, limit={}",
                    key, count, rateLimit.limit());
            throw new BusinessException(429, rateLimit.message());
        }

        return joinPoint.proceed();
    }

    private String buildKey(RateLimit rateLimit, Method method) {
        if (!rateLimit.key().isEmpty()) {
            // 自定义 key（简化：直接使用IP）
            try {
                ServletRequestAttributes attrs = (ServletRequestAttributes)
                        RequestContextHolder.getRequestAttributes();
                if (attrs != null) {
                    return rateLimit.key() + ":" +
                            attrs.getRequest().getRemoteAddr();
                }
            } catch (Exception ignored) {}
        }
        // 默认：方法名 + 用户ID
        try {
            Integer userId = SecurityUtils.getCurrentUserId();
            return method.getName() + ":" + userId;
        } catch (Exception e) {
            return method.getName() + ":anonymous";
        }
    }
}
