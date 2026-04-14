package com.mochu.framework.aspect;

import com.mochu.common.exception.BusinessException;
import com.mochu.framework.annotation.Idempotent;
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

/**
 * 幂等性切面 —— 基于 Redis SETNX 实现
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class IdempotentAspect {

    private final StringRedisTemplate redisTemplate;

    private static final String IDEMPOTENT_KEY_PREFIX = "idempotent:";
    private static final String HEADER_NAME = "X-Idempotency-Key";

    @Around("@annotation(com.mochu.framework.annotation.Idempotent)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Idempotent idempotent = method.getAnnotation(Idempotent.class);

        // 获取请求头中的幂等键
        ServletRequestAttributes attrs = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attrs.getRequest();
        String idempotencyKey = request.getHeader(HEADER_NAME);

        if (idempotencyKey == null || idempotencyKey.isEmpty()) {
            // 无幂等键，直接放行（向后兼容）
            log.warn("请求缺少 {} 头: {} {}",
                    HEADER_NAME, request.getMethod(), request.getRequestURI());
            return joinPoint.proceed();
        }

        // Redis SETNX 尝试占位
        String redisKey = IDEMPOTENT_KEY_PREFIX + idempotencyKey;
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(redisKey, "1", idempotent.timeout(), TimeUnit.SECONDS);

        if (Boolean.FALSE.equals(success)) {
            // 键已存在 -> 重复请求
            log.warn("重复请求被拦截: key={}, uri={}",
                    idempotencyKey, request.getRequestURI());
            throw new BusinessException(429, idempotent.message());
        }

        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            // 业务异常时删除幂等键，允许重试
            redisTemplate.delete(redisKey);
            throw e;
        }
    }
}
