package com.mochu.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门表 — 对照 V3.2 附录P.2
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends BaseEntity {

    /** 部门名称 */
    private String name;

    /** 上级部门ID，0为根 */
    private Integer parentId;

    /** 层级，根部门=1 */
    private Integer level;

    /** 路径如"/1/2/21/" */
    private String path;

    /** 排序号 */
    private Integer sort;

    /** 负责人ID */
    private Integer leaderId;

    /** 部门电话 */
    private String phone;

    /** 部门邮箱 */
    private String email;

    /** 备注 */
    private String remark;

    /** 1启用/0停用 */
    private Integer status;
}
