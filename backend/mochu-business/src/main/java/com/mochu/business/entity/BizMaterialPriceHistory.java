package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 物料价格历史表 — 对照 V3.2.0.06 DDL
 */
@Data
@TableName("biz_material_price_history")
public class BizMaterialPriceHistory {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer materialId;
    /** 供应商ID */
    private Integer supplierId;
    /** 价格 — DDL: price */
    private BigDecimal price;
    /** 税率 */
    private BigDecimal taxRate;
    /** 报价日期 */
    private LocalDate priceDate;
    /** 价格来源 */
    private String source;
    private String remark;
    private Integer creatorId;
    private LocalDateTime createdAt;
}
