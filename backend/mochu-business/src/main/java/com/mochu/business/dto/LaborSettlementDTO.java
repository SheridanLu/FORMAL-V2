package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LaborSettlementDTO {

    private Integer id;

    @NotNull(message = "项目ID不能为空")
    private Integer projectId;

    @NotNull(message = "劳务合同ID不能为空")
    private Integer contractId;

    @NotNull(message = "结算金额不能为空")
    private BigDecimal settlementAmount;

    private BigDecimal paidAmount;

    @NotNull(message = "申请付款金额不能为空")
    private BigDecimal applyPayAmount;
}
