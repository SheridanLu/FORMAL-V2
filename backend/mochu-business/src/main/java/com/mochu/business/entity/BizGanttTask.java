package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 甘特图任务表 — 对照 V3.2 P.40
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_gantt_task")
public class BizGanttTask extends BaseEntity {

    private Integer projectId;

    private Integer parentId;

    private String taskName;

    /** 1里程碑/2任务 */
    private Integer taskType;

    private LocalDate planStartDate;

    private LocalDate planEndDate;

    private LocalDate actualStartDate;

    private LocalDate actualEndDate;

    private BigDecimal progressPct;

    /** 进度描述 — V3.2.0.09 新增 */
    private String progressDesc;

    /** 依赖关系类型:FS/SS/FF/SF */
    private String dependencyType;

    private Integer dependencyTaskId;

    private Integer sortOrder;

    /** draft/pending/approved/locked */
    private String status;
}
