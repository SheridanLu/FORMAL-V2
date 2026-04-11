package com.mochu.bpm.dto;

import lombok.Data;

@Data
public class StartProcessDTO {
    private String bizType;
    private Integer bizId;
    private String bizNo;
    /** 流程变量（JSON字符串，可选） */
    private String variablesJson;
}
