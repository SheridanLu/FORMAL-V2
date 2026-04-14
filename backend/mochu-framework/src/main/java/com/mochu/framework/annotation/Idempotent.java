package com.mochu.framework.annotation;

import java.lang.annotation.*;

/**
 * 接口幂等性注解 —— 防止重复提交
 * 通过请求头 X-Idempotency-Key 实现
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /** 幂等键的过期时间（秒），默认5分钟 */
    int timeout() default 300;

    /** 重复提交时的提示信息 */
    String message() default "请勿重复提交";
}
