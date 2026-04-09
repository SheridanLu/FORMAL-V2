package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 劳务结算表 — 对照 V3.2 P.64
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_labor_settlement")
public class BizLaborSettlement extends BaseEntity {

    private String settlementNo;

    private Integer projectId;

    private Integer contractId;

    private BigDecimal settlementAmount;

    private BigDecimal paidAmount;

    private BigDecimal applyPayAmount;

    private String status;
}
