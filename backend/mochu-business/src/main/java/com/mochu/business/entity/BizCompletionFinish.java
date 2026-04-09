package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 完工验收表 — 对照 V3.2 P.62
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_completion_finish")
public class BizCompletionFinish extends BaseEntity {

    private Integer projectId;

    private String title;

    private LocalDate planFinishDate;

    private String finishContent;

    private String selfCheckResult;

    private String remainingIssues;

    private String status;
}
