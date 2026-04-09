package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 异常工单表 — 对照 V3.2 P.71
 * 注意：DDL 无 deleted 列，不能继承 BaseEntity
 */
@Data
@TableName("biz_exception_task")
public class BizExceptionTask {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String bizType;

    private Integer bizId;

    private String failReason;

    private Integer handlerId;

    private String resolveRemark;

    /** 1待处理 / 2已处理 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private Integer creatorId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updatedAt;
}
