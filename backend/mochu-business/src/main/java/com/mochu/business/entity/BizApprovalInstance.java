package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审批实例表 — 对照 V3.2 附录P.68
 */
@Data
@TableName("biz_approval_instance")
public class BizApprovalInstance {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 关联业务类型 */
    private String bizType;

    /** 关联业务单据ID */
    private Integer bizId;

    /** 关联流程定义ID */
    private Integer flowDefId;

    /** 当前审批节点序号 */
    private Integer currentNode;

    /** pending/approved/rejected/cancelled */
    private String status;

    /** 发起人ID */
    private Integer initiatorId;

    /** 当前节点进入时间(用于超时计算) */
    private LocalDateTime deadlineAt;

    /** 超时提醒级别: 0未提醒/1已24h/2已48h/3已72h */
    private Integer reminderLevel;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
