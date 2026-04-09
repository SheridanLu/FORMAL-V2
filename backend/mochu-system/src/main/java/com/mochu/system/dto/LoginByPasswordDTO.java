package com.mochu.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 密码登录请求 — 对照 V3.2 §4.1 login-by-password
 */
@Data
public class LoginByPasswordDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
