package com.mochu.system.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mochu.common.security.LoginUser;
import com.mochu.common.security.SecurityUtils;
import com.mochu.framework.annotation.AuditLog;
import com.mochu.system.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 审计日志切面 —— 自动记录标注了 @AuditLog 的方法调用
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogAspect {

    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(com.mochu.framework.annotation.AuditLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        AuditLog annotation = method.getAnnotation(AuditLog.class);

        Integer userId = null;
        String userName = null;
        try {
            LoginUser loginUser = SecurityUtils.getCurrentUser();
            if (loginUser != null) {
                userId = loginUser.getUserId();
                userName = loginUser.getRealName();
            }
        } catch (Exception e) {
            // 登录接口可能无用户上下文
        }

        String ipAddress = "";
        String requestId = "";
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes)
                    RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                ipAddress = getClientIp(request);
                requestId = request.getHeader("X-Request-Id");
            }
        } catch (Exception e) {
            log.warn("获取请求信息失败", e);
        }

        Integer bizId = extractBizId(joinPoint);

        String beforeData = null;
        if (annotation.saveBefore() && bizId != null) {
            beforeData = annotation.saveParams()
                    ? serializeArgs(joinPoint.getArgs()) : null;
        }

        Object result = joinPoint.proceed();

        String afterData = null;
        if (annotation.saveParams()) {
            afterData = serializeArgs(joinPoint.getArgs());
        }

        try {
            auditLogService.log(
                    userId, userName,
                    annotation.operateType(), annotation.operateModule(),
                    annotation.bizType(), bizId,
                    beforeData, afterData,
                    ipAddress, requestId
            );
        } catch (Exception e) {
            log.error("审计日志写入失败: {}", e.getMessage(), e);
        }

        return result;
    }

    private Integer extractBizId(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < paramNames.length; i++) {
            if ("id".equals(paramNames[i]) && args[i] instanceof Integer) {
                return (Integer) args[i];
            }
        }
        return null;
    }

    private String serializeArgs(Object[] args) {
        try {
            Map<String, Object> params = new HashMap<>();
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null
                        && !(args[i] instanceof HttpServletRequest)
                        && !(args[i] instanceof jakarta.servlet.http.HttpServletResponse)) {
                    params.put("arg" + i, args[i]);
                }
            }
            return objectMapper.writeValueAsString(params);
        } catch (Exception e) {
            return "{}";
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
