package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 产值报表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_progress_statement")
public class BizProgressStatement extends BaseEntity {

    private String statementNo;

    private Integer projectId;

    /** 期间，如 "2026-04" */
    private String period;

    private BigDecimal plannedAmount;

    private BigDecimal actualAmount;

    private BigDecimal completionRate;

    private String remark;

    /** draft/pending/approved/rejected */
    private String status;
}
