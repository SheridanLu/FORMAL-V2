package com.mochu.bpm.dto;

import lombok.Data;

@Data
public class OaRuleDTO {
    private String bizType;
    private String bizName;
    private String processDefKey;
    private Integer enabled;
}
