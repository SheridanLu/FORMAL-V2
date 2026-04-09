package com.mochu.system.vo;

import lombok.Data;

/**
 * 权限 VO
 */
@Data
public class PermissionVO {

    private Integer id;

    private String permCode;

    private String permName;

    private String module;

    private Integer permType;
}
