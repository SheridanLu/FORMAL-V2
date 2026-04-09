package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 报销单表 — 对照 V3.2 P.52
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_reimburse")
public class BizReimburse extends BaseEntity {

    private String reimburseNo;

    private String reimburseType;

    private BigDecimal amount;

    private Integer deptId;

    private Integer projectId;

    private String description;

    private String status;
}
