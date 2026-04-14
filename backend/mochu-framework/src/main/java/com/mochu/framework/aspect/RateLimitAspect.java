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
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitAspect {

    private final StringRedisTemplate redisTemplate;

    @Around("@annotation(com.mochu.framework.annotation.RateLimit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        String key = buildKey(rateLimit, method);
        String redisKey = "rate_limit:" + key;

        Long count = redisTemplate.opsForValue().increment(redisKey);
        if (count != null && count == 1) {
            redisTemplate.expire(redisKey, rateLimit.period(), TimeUnit.SECONDS);
        }

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
