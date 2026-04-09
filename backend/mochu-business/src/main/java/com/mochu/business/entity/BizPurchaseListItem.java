package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 采购清单明细子表 — 对照 V3.2 P.28
 */
@Data
@TableName("biz_purchase_list_item")
public class BizPurchaseListItem {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer listId;

    private Integer materialId;

    private String materialName;

    private String specModel;

    private String unit;

    private BigDecimal quantity;

    private BigDecimal estimatedPrice;

    private BigDecimal subtotal;

    private String remark;

    private LocalDateTime createdAt;
}
