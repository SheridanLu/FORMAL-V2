package com.mochu.business.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * P6: 收入拆分明细项DTO — 对照 V3.2.0.10 DDL: biz_income_split_item
 */
@Data
public class IncomeSplitItemDTO {

    /** 部门ID */
    private Integer deptId;

    /** 人员ID */
    private Integer userId;

    /** 分成比例 */
    private BigDecimal splitRatio;

    /** 分成金额 */
    private BigDecimal splitAmount;

    private String remark;
}
