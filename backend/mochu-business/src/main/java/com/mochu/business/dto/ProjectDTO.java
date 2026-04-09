package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProjectDTO {

    private Integer id;

    @NotBlank(message = "项目名称不能为空")
    private String projectName;

    private String projectAlias;

    @NotNull(message = "项目类型不能为空")
    private Integer projectType;

    @NotBlank(message = "合同类型不能为空")
    private String contractType;

    private String location;

    private BigDecimal amountWithTax;

    private BigDecimal amountWithoutTax;

    private BigDecimal taxAmount;

    private BigDecimal taxRate;

    private String clientName;

    private LocalDate planStartDate;

    private LocalDate planEndDate;

    private LocalDate warrantyDate;

    private Integer managerId;

    private BigDecimal investLimit;

    private LocalDate bidTime;

    private Integer sourceProjectId;

    private Integer costTargetProjectId;

    private String remark;
}
