package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退库单明细子表 — 对照 V3.2 P.35
 */
@Data
@TableName("biz_return_order_item")
public class BizReturnOrderItem {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer returnId;

    private Integer materialId;

    private String materialName;

    private String unit;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal subtotal;

    private LocalDateTime createdAt;
}
