package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 里程碑 DTO — 仅完成时间，支持多依赖
 */
@Data
public class MilestoneDTO {

    @NotNull(message = "项目ID不能为空")
    private Integer projectId;

    @NotBlank(message = "里程碑名称不能为空")
    private String milestoneName;

    @NotNull(message = "计划完成时间不能为空")
    private LocalDate deadline;

    private LocalDate actualEndDate;

    private Integer sortOrder;

    /** 前置依赖的里程碑ID列表 */
    private List<Integer> depMilestoneIds;
}
