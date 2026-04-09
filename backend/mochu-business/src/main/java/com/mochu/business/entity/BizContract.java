package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 合同表 — 对照 V3.2 P.19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_contract")
public class BizContract extends BaseEntity {

    private String contractNo;
    private String contractName;
    private String contractType;
    private Integer projectId;
    private Integer supplierId;
    private Integer templateId;
    private Integer tplVersionId;
    private BigDecimal amountWithTax;
    private BigDecimal amountWithoutTax;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private LocalDate signDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String partyA;
    private String partyB;
    private String status;
    private Integer parentContractId;
    private Integer purchaseListId;
    private String terminateReason;
    private LocalDateTime terminateTime;
    private Integer terminatorId;
    private String remark;

    @Version
    private Integer version;
}
