package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatementDTO {

    private Integer id;

    @NotNull(message = "项目ID不能为空")
    private Integer projectId;

    @NotNull(message = "合同ID不能为空")
    private Integer contractId;

    @NotBlank(message = "账期不能为空")
    private String period;

    @NotNull(message = "合同含税金额不能为空")
    private BigDecimal contractAmount;

    private BigDecimal progressRatio;

    private BigDecimal currentOutput;

    private BigDecimal cumulativeOutput;

    private BigDecimal currentCollection;

    private BigDecimal cumulativeCollection;
}
