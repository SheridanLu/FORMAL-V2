package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_inventory_transfer")
public class BizInventoryTransfer extends BaseEntity {
    private String transferNo;
    private Integer fromProjectId;
    private Integer toProjectId;
    private Integer materialId;
    private String materialName;
    private String unit;
    private BigDecimal qty;
    private BigDecimal avgPrice;
    private BigDecimal totalAmount;
    private String status;
    private String remark;
    private LocalDateTime confirmTime;
}
