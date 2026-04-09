package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 工资表 — 对照 V3.2 P.53
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_salary")
public class BizSalary extends BaseEntity {

    private Integer userId;

    private String salaryMonth;

    private BigDecimal baseSalary;

    private BigDecimal positionSalary;

    private BigDecimal performance;

    private BigDecimal allowance;

    private BigDecimal bonus;

    private BigDecimal deduction;

    private BigDecimal socialInsurance;

    private BigDecimal tax;

    private BigDecimal netSalary;

    private String status;
}
