package com.mochu.bpm.dto;

import lombok.Data;

@Data
public class ProcessDeployDTO {
    private String processDefKey;
    private String processName;
    private String bpmn20Xml;
    private String bizType;
    private Integer candidateStrategy;
    private String candidateParam;
    private String formConfig;
    private String remark;
}
