package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("biz_inventory_check_item")
public class BizInventoryCheckItem {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer checkId;
    private Integer materialId;
    private String materialName;
    private String spec;
    private String unit;
    private BigDecimal bookQuantity;
    private BigDecimal actualQuantity;
    private BigDecimal diffQuantity;
    private String diffReason;
    private LocalDateTime createdAt;
}
