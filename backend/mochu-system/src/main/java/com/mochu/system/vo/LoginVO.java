package com.mochu.system.vo;

import lombok.Data;

/**
 * 登录响应 — 对照 V3.2 §4.1 login response
 */
@Data
public class LoginVO {

    private String token;

    /** 是否需要强制修改密码 */
    private Boolean forceChangePwd;

    /** HMAC 签名密钥 — 登录后下发，前端存入 sessionStorage 用于敏感接口签名 */
    private String signSecret;
}
