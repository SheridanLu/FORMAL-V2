package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 采购清单表 — 对照 V3.2 P.27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_purchase_list")
public class BizPurchaseList extends BaseEntity {

    private String listNo;

    private Integer projectId;

    private BigDecimal totalAmount;

    private String status;

    private String remark;
}
