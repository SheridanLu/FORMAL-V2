package com.mochu.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审计日志表 — 对照 V3.2 附录P.14
 */
@Data
@TableName("sys_audit_log")
public class SysAuditLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer userId;

    private String userName;

    private String operateType;

    private String operateModule;

    private String bizType;

    private Integer bizId;

    private String beforeData;

    private String afterData;

    private String ipAddress;

    private String requestId;

    private LocalDateTime createdAt;
}
