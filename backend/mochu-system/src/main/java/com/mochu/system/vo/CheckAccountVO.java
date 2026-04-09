package com.mochu.system.vo;

import lombok.Data;

/**
 * 账号检查响应 — 对照 V3.2 §4.1 check-account
 */
@Data
public class CheckAccountVO {

    /** 账号是否存在 */
    private Boolean exists;

    /** 是否已锁定 */
    private Boolean locked;
}
