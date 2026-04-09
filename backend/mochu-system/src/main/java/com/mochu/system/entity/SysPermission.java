package com.mochu.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 权限表 — 对照 V3.2 附录P.4
 * 注意: 该表无审计字段(creator_id, created_at等), 不继承BaseEntity
 */
@Data
@TableName("sys_permission")
public class SysPermission {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 权限编码 模块:操作 */
    private String permCode;

    /** 权限名称 */
    private String permName;

    /** 所属模块 */
    private String module;

    /** 1功能权限/2数据权限 */
    private Integer permType;
}
