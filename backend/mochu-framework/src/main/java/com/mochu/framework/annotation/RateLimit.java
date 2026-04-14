package com.mochu.framework.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    /** 限流键（支持SpEL），默认按当前用户ID */
    String key() default "";
    /** 时间窗口内最大请求数 */
    int limit() default 10;
    /** 时间窗口（秒） */
    int period() default 60;
    /** 超限提示 */
    String message() default "请求过于频繁，请稍后再试";
}
