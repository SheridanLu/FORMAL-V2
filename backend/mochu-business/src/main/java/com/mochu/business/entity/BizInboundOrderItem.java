package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 入库单明细子表 — 对照 V3.2 P.31
 */
@Data
@TableName("biz_inbound_order_item")
public class BizInboundOrderItem {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer inboundId;

    private Integer materialId;

    private String materialName;

    private String specModel;

    private String unit;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal subtotal;

    private Integer contractMaterialId;

    private LocalDateTime createdAt;
}
