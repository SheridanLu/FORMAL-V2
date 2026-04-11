package com.mochu.business.dto;

import lombok.Data;

@Data
public class ReportTemplateDTO {
    private String reportName;
    private String category;
    private String chartType;
    private String sqlText;
    private String paramsJson;
    private String xField;
    private String yFields;
    private String description;
}
