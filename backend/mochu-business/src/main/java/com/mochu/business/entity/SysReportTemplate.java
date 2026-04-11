package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_report_template")
public class SysReportTemplate extends BaseEntity {
    private String reportName;
    private String category;
    private String chartType;
    private String sqlText;
    private String paramsJson;
    private String xField;
    private String yFields;
    private String description;
    private Integer status;
}
