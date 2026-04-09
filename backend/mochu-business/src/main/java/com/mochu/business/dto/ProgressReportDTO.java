package com.mochu.business.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProgressReportDTO {

    private Integer id;

    @NotNull(message = "项目ID不能为空")
    private Integer projectId;

    private LocalDate reportDate;

    @NotNull(message = "汇报内容不能为空")
    private String content;

    private BigDecimal progressRate;

    private String issues;

    private String nextPlan;

    private String remark;
}
