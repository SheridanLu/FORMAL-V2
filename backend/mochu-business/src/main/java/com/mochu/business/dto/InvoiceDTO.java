package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InvoiceDTO {

    private Integer id;

    @NotBlank(message = "发票号码不能为空")
    private String invoiceNo;

    @NotBlank(message = "发票类型不能为空")
    private String invoiceType;

    @NotNull(message = "发票金额不能为空")
    private BigDecimal amount;

    private BigDecimal taxRate;

    private BigDecimal taxAmount;

    @NotNull(message = "开票日期不能为空")
    private LocalDate invoiceDate;

    private String invoiceParty;

    @NotBlank(message = "业务类型不能为空")
    private String bizType;

    @NotNull(message = "业务单据ID不能为空")
    private Integer bizId;

    private Integer attachmentId;

    private Integer isCertified;

    private LocalDate certifiedDate;
}
