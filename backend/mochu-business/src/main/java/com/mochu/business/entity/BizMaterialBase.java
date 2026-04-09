package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 材料基础信息表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_material_base")
public class BizMaterialBase extends BaseEntity {

    private String materialCode;

    private String materialName;

    private String specModel;

    private String unit;

    private String category;

    private BigDecimal basePriceWithTax;

    private Integer taxRate;

    private String status;
}
