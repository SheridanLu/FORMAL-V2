package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审批会签(加签)表
 */
@Data
@TableName("biz_approval_cosign")
public class BizApprovalCosign {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 关联审批实例ID */
    private Integer instanceId;

    /** 节点序号 */
    private Integer nodeOrder;

    /** 会签人用户ID */
    private Integer cosignerId;

    /** pending/approved/rejected */
    private String status;

    /** 会签意见 */
    private String opinion;

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;
}
