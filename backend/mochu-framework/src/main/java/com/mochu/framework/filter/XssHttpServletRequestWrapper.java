package com.mochu.framework.filter;

import cn.hutool.http.HtmlUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

/**
 * XSS 请求包装器 —— 重写参数获取方法，自动清理 HTML 标签
 * <p>
 * 对 query-param / header 使用 HTML 实体编码；
 * 对 JSON 请求体做「值级别」清理（只转义字符串值，保持 JSON 结构完整）。
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

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

        // 判断是否为 JSON 请求
        String contentType = getContentType();
        String cleanedBody;
        if (contentType != null && contentType.contains("application/json")) {
            // JSON 请求体：只清理字符串值，保持 JSON 结构不变
            cleanedBody = cleanJsonBody(body);
        } else {
            // 非 JSON（form-urlencoded 等）：整体 escape
            cleanedBody = cleanXss(body);
        }

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
     * XSS 清理 — 使用 HTML 实体编码
     */
    private String cleanXss(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return HtmlUtil.escape(value);
    }

    /**
     * JSON body 值级别 XSS 清理 —— 遍历 JSON 树，只对字符串值做 escape，
     * 保持 JSON 结构（引号、花括号、方括号等）不被破坏。
     * 如果 body 不是合法 JSON，原样返回交给后续框架报错。
     */
    private String cleanJsonBody(String body) {
        try {
            JsonNode root = MAPPER.readTree(body);
            JsonNode cleaned = cleanNode(root);
            return MAPPER.writeValueAsString(cleaned);
        } catch (JsonProcessingException e) {
            // 非法 JSON，原样返回，让 Spring 的 HttpMessageConverter 抛出标准错误
            return body;
        }
    }

    private JsonNode cleanNode(JsonNode node) {
        if (node.isTextual()) {
            return new TextNode(cleanXss(node.textValue()));
        }
        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;
            Iterator<Map.Entry<String, JsonNode>> fields = obj.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                obj.set(entry.getKey(), cleanNode(entry.getValue()));
            }
            return obj;
        }
        if (node.isArray()) {
            ArrayNode arr = (ArrayNode) node;
            for (int i = 0; i < arr.size(); i++) {
                arr.set(i, cleanNode(arr.get(i)));
            }
            return arr;
        }
        // 数值、布尔、null 等原样返回
        return node;
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
