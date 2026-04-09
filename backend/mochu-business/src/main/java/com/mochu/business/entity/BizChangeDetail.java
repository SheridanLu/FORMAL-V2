package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 变更明细子表 — 对照 V3.2 P.43
 */
@Data
@TableName("biz_change_detail")
public class BizChangeDetail {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer changeId;

    private String itemName;

    private String specModel;

    private String unit;

    private BigDecimal planQuantity;

    private BigDecimal actualQuantity;

    private BigDecimal diffQuantity;

    private BigDecimal unitPrice;

    private BigDecimal subtotal;

    private LocalDateTime createdAt;
}
