package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 对账单表 — 对照 V3.2 P.44
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_statement")
public class BizStatement extends BaseEntity {

    private String statementNo;

    private Integer projectId;

    private Integer contractId;

    private String period;

    private BigDecimal contractAmount;

    private BigDecimal progressRatio;

    private BigDecimal currentOutput;

    private BigDecimal cumulativeOutput;

    private BigDecimal currentCollection;

    private BigDecimal cumulativeCollection;

    private String status;
}
