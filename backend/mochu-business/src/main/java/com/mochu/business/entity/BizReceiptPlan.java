package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 收款计划（回款督办）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_receipt_plan")
public class BizReceiptPlan extends BaseEntity {

    private Integer contractId;
    private Integer projectId;
    private Integer planNo;
    private BigDecimal planAmount;
    private LocalDate planDate;
    private BigDecimal actualAmount;
    private LocalDate actualDate;
    /** pending/partial/completed/overdue */
    private String status;
    private String remark;
}
