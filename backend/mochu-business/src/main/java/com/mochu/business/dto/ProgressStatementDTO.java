package com.mochu.business.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProgressStatementDTO {

    private Integer id;

    @NotNull(message = "项目ID不能为空")
    private Integer projectId;

    @NotNull(message = "期间不能为空")
    private String period;

    private BigDecimal plannedAmount;

    private BigDecimal actualAmount;

    private String remark;
}
