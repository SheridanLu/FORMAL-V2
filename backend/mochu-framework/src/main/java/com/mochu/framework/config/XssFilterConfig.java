package com.mochu.framework.config;

import com.mochu.framework.filter.XssFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * XSS 过滤器配置
 */
@Configuration
public class XssFilterConfig {

    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterRegistration() {
        FilterRegistrationBean<XssFilter> registration
                = new FilterRegistrationBean<>();

        // 排除路径（富文本编辑等）
        Set<String> excludePaths = Set.of(
                "/api/v1/admin/config"  // 系统配置可能含 HTML
        );

        registration.setFilter(new XssFilter(excludePaths));
        registration.addUrlPatterns("/api/*");
        registration.setName("xssFilter");
        registration.setOrder(1);  // 优先级高
        return registration;
    }
}
