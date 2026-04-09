package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 离职申请表 — 对照 V3.2 P.60
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_hr_resign")
public class BizHrResign extends BaseEntity {

    private String resignNo;

    private Integer userId;

    private String resignType;

    private LocalDate resignDate;

    private String resignReason;

    private String handoverStatus;

    private Integer handoverTo;

    private String status;
}
