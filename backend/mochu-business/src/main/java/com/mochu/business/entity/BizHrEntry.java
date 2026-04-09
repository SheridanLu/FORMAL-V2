package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 入职申请表 — 对照 V3.2 P.59
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_hr_entry")
public class BizHrEntry extends BaseEntity {

    private String entryNo;

    private String applicantName;

    private String phone;

    private Integer deptId;

    private String position;

    private LocalDate entryDate;

    private String education;

    private Integer workYears;

    private String idCardNo;

    private String status;
}
