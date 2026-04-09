package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 项目表 — 对照 V3.2 P.18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_project")
public class BizProject extends BaseEntity {

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

    private BigDecimal investLimit;

    private LocalDate bidTime;

    private Integer sourceProjectId;

    private Integer costTargetProjectId;

    private String remark;

    @Version
    private Integer version;
}
