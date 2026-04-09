package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 进度汇报
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_progress_report")
public class BizProgressReport extends BaseEntity {

    private String reportNo;

    private Integer projectId;

    private LocalDate reportDate;

    private String content;

    private BigDecimal progressRate;

    private String issues;

    private String nextPlan;

    private String remark;

    /** draft/pending/approved/rejected */
    private String status;
}
