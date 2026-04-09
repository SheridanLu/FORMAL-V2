package com.mochu.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户角色关联表 — 对照 V3.2 附录P.6
 */
@Data
@TableName("sys_user_role")
public class SysUserRole {

    private Integer userId;

    private Integer roleId;
}
