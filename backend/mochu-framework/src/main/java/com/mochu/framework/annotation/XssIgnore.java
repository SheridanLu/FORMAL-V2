package com.mochu.framework.annotation;

import java.lang.annotation.*;

/**
 * 标注在 Controller 方法上，排除 XSS 过滤
 * 用于富文本编辑等需要保留 HTML 的场景
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XssIgnore {
}
