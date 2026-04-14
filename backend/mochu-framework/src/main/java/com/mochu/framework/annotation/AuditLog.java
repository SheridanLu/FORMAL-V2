package com.mochu.framework.annotation;

import java.lang.annotation.*;

/**
 * 审计日志注解 —— 标注在 Controller 方法上，自动记录操作日志
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {

    /** 操作类型：CREATE, UPDATE, DELETE, EXPORT, LOGIN, STATUS_CHANGE 等 */
    String operateType();

    /** 操作模块（中文）：如"项目管理"、"合同管理" */
    String operateModule();

    /** 关联业务类型：如"project"、"contract"，用于回溯 */
    String bizType() default "";

    /** 是否记录请求参数（默认true） */
    boolean saveParams() default true;

    /** 是否记录变更前数据（UPDATE/DELETE时需要） */
    boolean saveBefore() default false;
}
