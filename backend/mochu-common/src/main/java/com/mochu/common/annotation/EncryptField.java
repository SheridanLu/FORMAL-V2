package com.mochu.common.annotation;

import java.lang.annotation.*;

/**
 * 标注在 Entity 的 String 字段上，表示该字段需要加密存储
 * 配合 EncryptedStringTypeHandler 使用
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EncryptField {
}
