package com.mochu.bpm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bpm_task_ext")
public class BpmTaskExt extends BaseEntity {
    private String processInstId;
    private String taskId;
    private String bizType;
    private Integer bizId;
    private String bizNo;
    private Integer initiatorId;
    private Integer result;
    private LocalDateTime endTime;
}
