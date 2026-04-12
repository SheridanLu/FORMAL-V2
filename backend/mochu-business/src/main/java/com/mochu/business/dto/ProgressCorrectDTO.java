package com.mochu.business.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 进度校正 DTO
 */
@Data
public class ProgressCorrectDTO {

    @NotNull(message = "项目ID不能为空")
    private Integer projectId;

    @NotNull(message = "实际进度不能为空")
    private BigDecimal actualRate;

    private String remark;
}
