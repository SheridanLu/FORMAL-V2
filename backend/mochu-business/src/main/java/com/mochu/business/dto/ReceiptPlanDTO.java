package com.mochu.business.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ReceiptPlanDTO {
    @NotNull(message = "合同ID不能为空")
    private Integer contractId;
    private Integer projectId;
    @NotNull(message = "期次不能为空")
    private Integer planNo;
    @NotNull(message = "计划金额不能为空")
    private BigDecimal planAmount;
    @NotNull(message = "计划日期不能为空")
    private LocalDate planDate;
    private String remark;
}
