package com.mochu.bpm.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProcessInstanceVO {
    private String processInstId;
    private String processDefKey;
    private String processDefName;
    private String bizType;
    private Integer bizId;
    private String bizNo;
    private Integer initiatorId;
    private String initiatorName;
    private Integer result;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<TaskRecordVO> records;
}
