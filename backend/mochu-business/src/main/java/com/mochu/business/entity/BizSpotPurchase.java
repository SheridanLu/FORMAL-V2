package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 零星采购表 — 对照 V3.2 P.29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_spot_purchase")
public class BizSpotPurchase extends BaseEntity {

    private String purchaseNo;

    private Integer projectId;

    /** 采购物品名称 (DDL: item_name) */
    private String itemName;

    private String specModel;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    /** 金额 (DDL: amount) */
    private BigDecimal amount;

    /** 供应商名称 (DDL: supplier_name VARCHAR) */
    private String supplierName;

    private String status;

    private String remark;
}
