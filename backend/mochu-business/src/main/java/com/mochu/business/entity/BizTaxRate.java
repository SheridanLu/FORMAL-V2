package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 个税税率表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_tax_rate")
public class BizTaxRate extends BaseEntity {

    private Integer level;

    private BigDecimal minIncome;

    private BigDecimal maxIncome;

    private BigDecimal rate;

    private BigDecimal deduction;
}
