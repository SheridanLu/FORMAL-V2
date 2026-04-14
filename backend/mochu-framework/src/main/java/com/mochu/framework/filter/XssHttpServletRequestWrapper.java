package com.mochu.framework.filter;

import cn.hutool.http.HtmlUtil;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * XSS 请求包装器 —— 重写参数获取方法，自动清理 HTML 标签
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return cleanXss(value);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) return null;
        String[] cleaned = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            cleaned[i] = cleanXss(values[i]);
        }
        return cleaned;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return cleanXss(value);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        // 读取原始请求体
        String body = readBody(super.getInputStream());
        if (body == null || body.isEmpty()) {
            return super.getInputStream();
        }

        // 清理 JSON 中的 XSS 内容
        String cleanedBody = cleanXss(body);
        byte[] bytes = cleanedBody.getBytes(StandardCharsets.UTF_8);

        return new ServletInputStream() {
            private final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

            @Override
            public int read() { return bais.read(); }

            @Override
            public boolean isFinished() { return bais.available() == 0; }

            @Override
            public boolean isReady() { return true; }

            @Override
            public void setReadListener(ReadListener listener) {
                // 非异步场景不实现
            }
        };
    }

    /**
     * XSS 清理 — 使用 HTML 实体编码代替标签剥离
     * #2 fix: cleanHtmlTag 仅剥离标签，无法防御实体编码/事件处理器注入，
     * 改用 escape() 做 HTML 实体编码，既防 XSS 又保留数据完整性
     */
    private String cleanXss(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return HtmlUtil.escape(value);
    }

    private String readBody(ServletInputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
