package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 合同物料清单表 — 对照 V3.2.0.05 DDL
 */
@Data
@TableName("biz_contract_material")
public class BizContractMaterial {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer contractId;
    private Integer materialId;
    private String materialName;
    /** 规格型号 — DDL: spec_model */
    private String specModel;
    private String unit;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
