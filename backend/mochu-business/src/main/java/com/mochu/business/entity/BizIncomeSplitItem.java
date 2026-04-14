package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收入分成明细表 — 对照 V3.2.0.10 DDL
 */
@Data
@TableName("biz_income_split_item")
public class BizIncomeSplitItem {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer splitId;
    /** 部门ID */
    private Integer deptId;
    /** 人员ID */
    private Integer userId;
    /** 分成比例 — DDL: split_ratio */
    private BigDecimal splitRatio;
    /** 分成金额 — DDL: split_amount */
    private BigDecimal splitAmount;
    private String remark;
    private LocalDateTime createdAt;
}
