package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成本台账表 — 对照 V3.2 P.50
 * <p>
 * 注意：该表没有 deleted 字段（无逻辑删除），也没有 updated_at，
 * 因此不继承 BaseEntity，直接映射所有字段。
 */
@Data
@TableName("biz_cost_ledger")
public class BizCostLedger {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer projectId;

    private String costType;

    private String costSubtype;

    private BigDecimal amount;

    private String bizType;

    private Integer bizId;

    private LocalDateTime collectTime;

    private Integer creatorId;

    private LocalDateTime createdAt;
}
