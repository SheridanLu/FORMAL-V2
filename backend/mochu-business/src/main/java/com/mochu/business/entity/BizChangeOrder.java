package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 变更管理主表 — 对照 V3.2 P.42
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_change_order")
public class BizChangeOrder extends BaseEntity {

    private String changeNo;

    /** 变更类型:visa/owner_change/overage/labor_visa */
    private String changeType;

    private Integer projectId;

    private Integer contractId;

    private String title;

    private String description;

    private BigDecimal totalAmount;

    /** draft/pending/approved/rejected */
    private String status;
}
