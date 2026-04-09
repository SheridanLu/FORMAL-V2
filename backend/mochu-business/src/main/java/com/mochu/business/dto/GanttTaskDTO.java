package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class GanttTaskDTO {

    private Integer id;

    @NotNull(message = "项目ID不能为空")
    private Integer projectId;

    private Integer parentId;

    @NotBlank(message = "任务名称不能为空")
    private String taskName;

    @NotNull(message = "任务类型不能为空")
    private Integer taskType;

    private LocalDate planStartDate;

    private LocalDate planEndDate;

    private LocalDate actualStartDate;

    private LocalDate actualEndDate;

    private BigDecimal progressPct;

    private String dependencyType;

    private Integer dependencyTaskId;

    private Integer sortOrder;
}
