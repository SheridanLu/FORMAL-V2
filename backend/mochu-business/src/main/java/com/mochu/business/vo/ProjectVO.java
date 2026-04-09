package com.mochu.business.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProjectVO {

    private Integer id;

    private String projectNo;

    private String projectName;

    private String projectAlias;

    private Integer projectType;

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

    private String status;

    private Integer managerId;

    private String managerName;

    private BigDecimal investLimit;

    private LocalDate bidTime;

    private Integer sourceProjectId;

    private Integer costTargetProjectId;

    private String remark;

    private Integer version;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
