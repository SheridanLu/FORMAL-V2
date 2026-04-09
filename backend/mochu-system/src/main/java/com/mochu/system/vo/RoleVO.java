package com.mochu.system.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色 VO
 */
@Data
public class RoleVO {

    private Integer id;

    private String roleCode;

    private String roleName;

    private Integer dataScope;

    private String remark;

    private Integer status;

    private LocalDateTime createdAt;

    /** 权限ID列表 */
    private List<Integer> permissionIds;
}
