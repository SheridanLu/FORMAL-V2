package com.mochu.framework.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Set;

/**
 * XSS 过滤器 —— 对所有请求进行 HTML 清理
 */
@Slf4j
public class XssFilter implements Filter {

    /** 排除路径集合（富文本接口等） */
    private final Set<String> excludePaths;

    public XssFilter(Set<String> excludePaths) {
        this.excludePaths = excludePaths;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String uri = httpRequest.getRequestURI();

        // 检查是否在排除路径中
        if (isExcluded(uri)) {
            chain.doFilter(request, response);
            return;
        }

        // 只过滤 POST/PUT/PATCH 请求
        String method = httpRequest.getMethod().toUpperCase();
        if ("GET".equals(method) || "DELETE".equals(method)
                || "OPTIONS".equals(method) || "HEAD".equals(method)) {
            chain.doFilter(request, response);
            return;
        }

        // 包装请求
        chain.doFilter(new XssHttpServletRequestWrapper(httpRequest), response);
    }

    private boolean isExcluded(String uri) {
        if (excludePaths == null || excludePaths.isEmpty()) {
            return false;
        }
        return excludePaths.stream().anyMatch(uri::startsWith);
    }
}
