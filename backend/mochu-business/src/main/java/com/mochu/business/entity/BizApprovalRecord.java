package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审批记录表 — 对照 V3.2 附录P.69
 */
@Data
@TableName("biz_approval_record")
public class BizApprovalRecord {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 关联审批实例ID */
    private Integer instanceId;

    /** 节点序号 */
    private Integer nodeOrder;

    /** 节点名称 */
    private String nodeName;

    /** 审批人ID */
    private Integer approverId;

    /** 操作类型:approve/reject/cancel/delegate/read/cc */
    private String action;

    /** 审批意见 */
    private String opinion;

    /** 转办来源用户ID */
    private Integer delegateFromId;

    private LocalDateTime createdAt;
}
