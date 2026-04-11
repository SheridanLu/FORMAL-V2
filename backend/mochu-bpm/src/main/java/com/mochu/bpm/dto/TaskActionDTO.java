package com.mochu.bpm.dto;

import lombok.Data;

@Data
public class TaskActionDTO {
    /** 审批意见 */
    private String comment;
    /** 转办/委派目标用户ID */
    private Integer targetUserId;
    /** 加签用户ID列表（逗号分隔） */
    private String addSignUserIds;
}
