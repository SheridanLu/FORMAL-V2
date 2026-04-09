package com.mochu.business.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ReceiptDTO {

    private Integer id;

    @NotNull(message = "项目ID不能为空")
    private Integer projectId;

    private Integer contractId;

    @NotNull(message = "收款金额不能为空")
    private BigDecimal amount;

    private LocalDate receiptDate;

    private String payer;

    private String receiptMethod;

    private String remark;
}
