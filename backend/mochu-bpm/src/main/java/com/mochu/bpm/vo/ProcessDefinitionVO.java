package com.mochu.bpm.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProcessDefinitionVO {
    private String id;
    private String key;
    private String name;
    private Integer version;
    private String category;
    private String deploymentId;
    private Boolean suspended;
    private LocalDateTime deployTime;
    /** 业务扩展信息 */
    private String bizType;
    private Integer candidateStrategy;
    private String candidateParam;
    private String formConfig;
    private String remark;
    private Integer extStatus;
}
