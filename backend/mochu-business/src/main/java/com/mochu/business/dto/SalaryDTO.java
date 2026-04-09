package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalaryDTO {

    private Integer id;

    @NotNull(message = "员工ID不能为空")
    private Integer userId;

    @NotBlank(message = "工资月份不能为空")
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

    /* ---------- 查询条件 ---------- */

    private Integer page;

    private Integer size;

    private String status;
}
