package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 报表订阅配置表 — 对照 V3.2.0.17 Flyway 迁移
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_report_subscribe")
public class BizReportSubscribe extends BaseEntity {

    private Integer userId;

    /** 报表类型 */
    private String reportType;

    /** daily/weekly/monthly */
    private String frequency;

    /** 报表参数JSON */
    private String paramsJson;

    /** 1=启用 0=停用 */
    private Integer status;
}
