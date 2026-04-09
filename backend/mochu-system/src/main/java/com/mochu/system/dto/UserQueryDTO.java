package com.mochu.system.dto;

import lombok.Data;

/**
 * 用户分页查询请求
 */
@Data
public class UserQueryDTO {

    private String username;

    private String realName;

    private String phone;

    private Integer deptId;

    private Integer status;

    private Integer page;

    private Integer size;
}
