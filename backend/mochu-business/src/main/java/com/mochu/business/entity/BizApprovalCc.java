package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审批抄送/阅办/阅知表
 */
@Data
@TableName("biz_approval_cc")
public class BizApprovalCc {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 关联审批实例ID */
    private Integer instanceId;

    /** 接收人用户ID */
    private Integer userId;

    /** read_handle阅办 / read_ack阅知 */
    private String ccType;

    /** 0未读/1已读 */
    private Integer isRead;

    /** 0未处理/1已处理(仅阅办) */
    private Integer isHandled;

    private LocalDateTime createdAt;

    private LocalDateTime handledAt;
}
