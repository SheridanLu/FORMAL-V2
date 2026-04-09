package com.mochu.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 委托代理表 — 对照 V3.2 P.7
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_delegation")
public class SysDelegation extends BaseEntity {

    private Integer delegatorId;

    private Integer delegateeId;

    private String permissionCodes;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String remark;

    private Integer status;
}
