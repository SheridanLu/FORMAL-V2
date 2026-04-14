package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("biz_inventory_record")
public class BizInventoryRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer projectId;
    private Integer materialId;
    private String bizType;       // inbound/outbound/return/check_gain/check_loss
    private Integer bizId;
    private String bizNo;
    private Integer direction;    // 1=入, -1=出
    private BigDecimal quantity;
    private BigDecimal beforeQuantity;
    private BigDecimal afterQuantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}
