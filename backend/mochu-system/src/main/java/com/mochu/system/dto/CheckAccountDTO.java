package com.mochu.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 账号检查请求 — 对照 V3.2 §4.1 check-account
 */
@Data
public class CheckAccountDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;
}
