package com.mochu.common.constant;

/**
 * 系统常量 — 对照 V3.2 §3.10 Redis Key 规范 + §3.3 分页参数
 */
public final class Constants {

    private Constants() {}

    // ========== 分页 ==========
    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    // ========== Redis Key 前缀 ==========
    /** 用户 Token: auth:token:{userId}:{clientType} */
    public static final String REDIS_TOKEN_PREFIX = "auth:token:";

    /** 用户权限列表: user:permissions:{userId} */
    public static final String REDIS_PERMISSIONS_PREFIX = "user:permissions:";

    /** 登录失败计数: auth:login_fail:{username} */
    public static final String REDIS_LOGIN_FAIL_PREFIX = "auth:login_fail:";

    /** 短信验证码: sms:phone:{phone} */
    public static final String REDIS_SMS_PREFIX = "sms:phone:";

    /** 编号自增: biz_no:{前缀}:{日期} */
    public static final String REDIS_BIZ_NO_PREFIX = "biz_no:";

    /** 通讯录缓存: contact:list:{deptId} */
    public static final String REDIS_CONTACT_PREFIX = "contact:list:";

    /** 待办数量: home:todo_count:{userId} */
    public static final String REDIS_TODO_COUNT_PREFIX = "home:todo_count:";

    // ========== Redis TTL (秒) ==========
    public static final long TOKEN_EXPIRE_SECONDS = 30L * 24 * 60 * 60; // 30天
    public static final long TOKEN_REFRESH_THRESHOLD_SECONDS = 7L * 24 * 60 * 60; // 7天
    public static final long LOGIN_FAIL_EXPIRE_SECONDS = 30L * 60; // 30分钟
    public static final long SMS_EXPIRE_SECONDS = 5L * 60; // 5分钟
    public static final long SMS_RESEND_INTERVAL_SECONDS = 60L; // 60秒限频
    public static final long CONTACT_CACHE_SECONDS = 30L * 60; // 30分钟
    public static final long TODO_COUNT_CACHE_SECONDS = 5L * 60; // 5分钟

    // ========== 安全 ==========
    public static final int MAX_LOGIN_ATTEMPTS = 5;
    public static final long LOCK_DURATION_SECONDS = 30L * 60; // 30分钟

    // ========== 数据权限 ==========
    public static final int DATA_SCOPE_ALL = 1;
    public static final int DATA_SCOPE_DEPT = 2;
    public static final int DATA_SCOPE_PROJECT = 3;
    public static final int DATA_SCOPE_SELF = 4;
    public static final int DATA_SCOPE_CUSTOM = 5;
}
