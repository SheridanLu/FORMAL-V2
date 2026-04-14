package com.mochu.framework.annotation;

import java.lang.annotation.*;

/**
 * 数据权限注解 —— 标注在 Mapper 方法或 Service 方法上
 * 拦截器会根据当前用户的 dataScope 自动拼接 WHERE 条件
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /** 部门表别名，用于拼接 dept_id 条件 */
    String deptAlias() default "";

    /** 用户表别名，用于拼接 creator_id 条件 */
    String userAlias() default "";

    /** 项目表别名，用于拼接 project_id 条件 */
    String projectAlias() default "";
}
