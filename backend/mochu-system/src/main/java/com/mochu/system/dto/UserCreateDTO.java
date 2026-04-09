package com.mochu.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建用户请求 — 对照 V3.2 P.1 字段定义
 */
@Data
public class UserCreateDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "姓名不能为空")
    private String realName;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    private String email;

    @NotNull(message = "部门不能为空")
    private Integer deptId;

    private String position;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String avatar;

    private Integer status;

    private Integer flagContact;

    private Integer privacyMode;

    private Integer forceChangePwd;

    private String wxUserid;
}
