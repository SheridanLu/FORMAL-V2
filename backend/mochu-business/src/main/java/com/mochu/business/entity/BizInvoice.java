package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 发票管理表 — 对照 V3.2 P.51
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_invoice")
public class BizInvoice extends BaseEntity {

    private String invoiceNo;

    private String invoiceType;

    private BigDecimal amount;

    private BigDecimal taxRate;

    private BigDecimal taxAmount;

    private LocalDate invoiceDate;

    private String invoiceParty;

    private String bizType;

    private Integer bizId;

    private Integer attachmentId;

    private Integer isCertified;

    private LocalDate certifiedDate;

    private String status;
}
