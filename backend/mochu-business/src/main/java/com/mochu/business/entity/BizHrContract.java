package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 人员合同表 — 对照 V3.2 P.57
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_hr_contract")
public class BizHrContract extends BaseEntity {

    private Integer userId;

    private String contractType;

    private LocalDate startDate;

    private LocalDate endDate;

    private String status;

    private Integer renewalId;

    private Integer contractFileId;
}
