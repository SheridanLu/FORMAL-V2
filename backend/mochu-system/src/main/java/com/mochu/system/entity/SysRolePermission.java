package com.mochu.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色权限关联表 — 对照 V3.2 附录P.5
 */
@Data
@TableName("sys_role_permission")
public class SysRolePermission {

    private Integer roleId;

    private Integer permissionId;
}
