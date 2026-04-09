package com.mochu.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 重置密码请求 — 对照 V3.2 §4.1 reset-password
 */
@Data
public class ResetPasswordDTO {

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotBlank(message = "验证码不能为空")
    private String smsCode;

    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
