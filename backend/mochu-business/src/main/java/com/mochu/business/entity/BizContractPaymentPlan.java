package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_contract_payment_plan")
public class BizContractPaymentPlan extends BaseEntity {
    private Integer contractId;
    private Integer planNo;
    private String planName;
    private BigDecimal planAmount;
    private LocalDate planDate;
    private BigDecimal actualAmount;
    private LocalDate actualDate;
    private String status;  // pending/paid/overdue
    private String remark;
}
