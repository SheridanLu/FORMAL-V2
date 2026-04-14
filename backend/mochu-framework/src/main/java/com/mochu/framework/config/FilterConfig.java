package com.mochu.framework.config;

import com.mochu.framework.filter.RequestHeaderValidationFilter;
import com.mochu.framework.filter.SignatureVerificationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * P5: 请求安全过滤器注册配置
 * - RequestHeaderValidationFilter: X-Request-Id / X-Client-Type / X-Idempotency-Key 校验
 * - SignatureVerificationFilter: HMAC-SHA256 签名验证（敏感操作）
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<RequestHeaderValidationFilter> headerValidationFilter() {
        FilterRegistrationBean<RequestHeaderValidationFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new RequestHeaderValidationFilter());
        reg.addUrlPatterns("/api/*");
        reg.setName("headerValidationFilter");
        reg.setOrder(0); // 最先执行
        return reg;
    }

    @Bean
    public FilterRegistrationBean<SignatureVerificationFilter> signatureFilter(
            SignatureVerificationFilter filter) {
        FilterRegistrationBean<SignatureVerificationFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(filter);
        reg.addUrlPatterns("/api/*");
        reg.setName("signatureVerificationFilter");
        reg.setOrder(2); // 在请求头校验之后
        return reg;
    }
}
