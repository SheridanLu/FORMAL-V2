package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_receipt")
public class BizReceipt extends BaseEntity {
    private String receiptNo;
    private Integer projectId;
    private Integer contractId;
    private BigDecimal amount;
    private LocalDate receiptDate;
    private String payer;
    private String receiptMethod;
    private String remark;
    private String status;
}
