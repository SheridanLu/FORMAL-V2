package com.mochu.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 统一响应格式 — 对照 V3.2 §3.3
 * {code, message, data, timestamp, errors}
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class R<T> {

    private int code;
    private String message;
    private T data;
    private long timestamp;
    private List<FieldError> errors;

    private R() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> R<T> ok() {
        R<T> r = new R<>();
        r.code = 200;
        r.message = "操作成功";
        return r;
    }

    public static <T> R<T> ok(T data) {
        R<T> r = ok();
        r.data = data;
        return r;
    }

    public static <T> R<T> ok(String message, T data) {
        R<T> r = ok();
        r.message = message;
        r.data = data;
        return r;
    }

    public static <T> R<T> fail(int code, String message) {
        R<T> r = new R<>();
        r.code = code;
        r.message = message;
        return r;
    }

    public static <T> R<T> fail(int code, String message, List<FieldError> errors) {
        R<T> r = fail(code, message);
        r.errors = errors;
        return r;
    }

    /**
     * 字段校验错误 — 对照 V3.2 §3.3 异常响应格式
     */
    @Data
    public static class FieldError {
        private String field;
        private String message;

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }
}
