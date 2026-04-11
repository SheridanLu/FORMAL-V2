package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_inventory_alert")
public class BizInventoryAlert extends BaseEntity {
    private Integer projectId;
    private Integer materialId;
    private String materialName;
    private String unit;
    private BigDecimal safetyQty;
    private BigDecimal minQty;
    private Integer alertEnabled;
}
