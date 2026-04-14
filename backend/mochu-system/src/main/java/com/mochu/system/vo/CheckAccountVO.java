package com.mochu.system.vo;

import lombok.Data;

/**
 * 账号检查响应 — 对照 V3.2 §4.1 check-account
 * P5 增强: 增加 loginType 和 maskedPhone
 */
@Data
public class CheckAccountVO {

    /** 账号是否存在 */
    private Boolean exists;

    /** 是否已锁定 */
    private Boolean locked;

    /** 登录方式: password / sms */
    private String loginType;

    /** 脱敏手机号, 如 138****5678 */
    private String maskedPhone;
}
