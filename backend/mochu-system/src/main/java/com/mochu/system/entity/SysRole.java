package com.mochu.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色表 — 对照 V3.2 附录P.3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    /** 角色编码 */
    private String roleCode;

    /** 角色名称 */
    private String roleName;

    /** 数据权限:1全部/2本部门/3本项目/4仅个人/5指定范围 */
    private Integer dataScope;

    /** 备注 */
    private String remark;

    /** 1启用/0禁用 */
    private Integer status;
}
