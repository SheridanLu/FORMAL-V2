package com.mochu.business.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ContractPaymentPlanDTO {
    private Integer planNo;
    /** 付款条件说明 — 对照 DDL: condition_desc */
    private String conditionDesc;
    @NotNull(message = "计划金额不能为空")
    private BigDecimal planAmount;
    private LocalDate planDate;
    private String remark;
}
