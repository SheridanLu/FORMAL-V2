package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 付款申请表 — 对照 V3.2 P.47
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_payment_apply")
public class BizPaymentApply extends BaseEntity {

    private String paymentNo;

    private String paymentType;

    private Integer projectId;

    private Integer contractId;

    private Integer statementId;

    private BigDecimal amount;

    private String payeeName;

    private String payeeBank;

    private String payeeAccount;

    private String paymentMethod;

    private String status;

    private LocalDateTime confirmTime;

    private Integer confirmerId;

    private String confirmRemark;

    private String remark;

    @Version
    private Integer version;
}
