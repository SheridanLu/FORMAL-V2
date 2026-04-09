package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReimburseDTO {

    private Integer id;

    @NotBlank(message = "报销类型不能为空")
    private String reimburseType;

    @NotNull(message = "报销金额不能为空")
    private BigDecimal amount;

    @NotNull(message = "部门ID不能为空")
    private Integer deptId;

    private Integer projectId;

    private String description;
}
