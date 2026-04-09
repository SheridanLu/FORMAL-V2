package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentApplyDTO {

    private Integer id;

    @NotBlank(message = "付款类型不能为空")
    private String paymentType;

    @NotNull(message = "项目ID不能为空")
    private Integer projectId;

    @NotNull(message = "合同ID不能为空")
    private Integer contractId;

    private Integer statementId;

    @NotNull(message = "付款金额不能为空")
    private BigDecimal amount;

    @NotBlank(message = "收款方名称不能为空")
    private String payeeName;

    private String payeeBank;

    private String payeeAccount;

    private String paymentMethod;

    private String remark;
}
