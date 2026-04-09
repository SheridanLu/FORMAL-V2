package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 库存表 — 对照 V3.2 P.36
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_inventory")
public class BizInventory extends BaseEntity {

    private Integer projectId;

    private Integer materialId;

    private String materialName;

    private String unit;

    private BigDecimal currentQuantity;

    private BigDecimal avgPrice;

    private BigDecimal totalAmount;
}
