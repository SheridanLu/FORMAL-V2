package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库存变动记录表 — 对照 V3.2.0.08 DDL
 */
@Data
@TableName("biz_inventory_record")
public class BizInventoryRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer projectId;
    private Integer materialId;
    /** inbound/outbound/return/check/transfer — DDL: record_type */
    private String recordType;
    /** 变动数量（正入负出） */
    private BigDecimal quantity;
    /** 变动前数量 — DDL: before_qty */
    private BigDecimal beforeQty;
    /** 变动后数量 — DDL: after_qty */
    private BigDecimal afterQty;
    /** 关联业务类型 */
    private String bizType;
    /** 关联业务ID */
    private Integer bizId;
    private String remark;
    private Integer creatorId;
    private LocalDateTime createdAt;
}
