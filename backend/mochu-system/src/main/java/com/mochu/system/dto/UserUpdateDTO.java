package com.mochu.system.dto;

import lombok.Data;

/**
 * 更新用户请求 — 对照 V3.2 P.1 字段定义（不含username/password）
 */
@Data
public class UserUpdateDTO {

    private Integer id;

    private String realName;

    private String phone;

    private String email;

    private Integer deptId;

    private String position;

    private String avatar;

    private Integer status;

    private Integer flagContact;

    private Integer privacyMode;

    private Integer forceChangePwd;

    private String wxUserid;
}
