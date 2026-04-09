package com.mochu.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 短信登录请求 — 对照 V3.2 §4.1 login-by-sms
 */
@Data
public class LoginBySmsDTO {

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotBlank(message = "验证码不能为空")
    private String smsCode;
}
