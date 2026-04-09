package com.mochu.business.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalaryConfigDTO {

    private Integer id;

    @NotNull(message = "薪资等级不能为空")
    private String grade;

    @NotNull(message = "等级名称不能为空")
    private String gradeName;

    @NotNull(message = "基本工资不能为空")
    private BigDecimal baseSalary;

    private BigDecimal allowance;

    private String remark;

    /* ---------- 查询条件 ---------- */

    private Integer page;

    private Integer size;

    private String status;
}
