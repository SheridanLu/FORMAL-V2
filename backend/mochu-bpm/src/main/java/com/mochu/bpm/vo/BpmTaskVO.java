package com.mochu.bpm.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BpmTaskVO {
    private String taskId;
    private String taskName;
    private String processInstId;
    private String processDefKey;
    private String processDefName;
    private String assignee;
    private Integer assigneeId;
    private String assigneeName;
    private LocalDateTime createTime;
    /** 关联业务信息 */
    private String bizType;
    private Integer bizId;
    private String bizNo;
    private Integer initiatorId;
    private String initiatorName;
}
