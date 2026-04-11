package com.mochu.bpm.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskRecordVO {
    private String taskId;
    private String taskName;
    private Integer assigneeId;
    private String assigneeName;
    private String comment;
    private Integer action;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
