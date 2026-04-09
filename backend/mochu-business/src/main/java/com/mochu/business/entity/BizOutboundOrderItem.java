package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 出库单明细子表 — 对照 V3.2 P.33
 */
@Data
@TableName("biz_outbound_order_item")
public class BizOutboundOrderItem {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer outboundId;

    private Integer materialId;

    private String materialName;

    private String unit;

    private BigDecimal quantity;

    private BigDecimal avgPrice;

    private BigDecimal subtotal;

    private LocalDateTime createdAt;
}
