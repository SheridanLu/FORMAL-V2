package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 合同付款计划表 — 对照 V3.2.0.04 DDL
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_contract_payment_plan")
public class BizContractPaymentPlan extends BaseEntity {
    private Integer contractId;
    private Integer planNo;
    private BigDecimal planAmount;
    private LocalDate planDate;
    /** 付款条件说明 — DDL: condition_desc */
    private String conditionDesc;
    private BigDecimal actualAmount;
    private LocalDate actualDate;
    private String status;  // pending/paid/overdue
    private String remark;
}
