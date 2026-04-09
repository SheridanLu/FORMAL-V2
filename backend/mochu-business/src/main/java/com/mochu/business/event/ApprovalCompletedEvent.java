package com.mochu.business.event;

import org.springframework.context.ApplicationEvent;

/**
 * 审批完成事件 — 通知业务模块更新状态
 */
public class ApprovalCompletedEvent extends ApplicationEvent {

    private final String bizType;
    private final Integer bizId;
    private final String finalStatus;

    public ApprovalCompletedEvent(Object source, String bizType, Integer bizId, String finalStatus) {
        super(source);
        this.bizType = bizType;
        this.bizId = bizId;
        this.finalStatus = finalStatus;
    }

    public String getBizType() { return bizType; }
    public Integer getBizId() { return bizId; }
    public String getFinalStatus() { return finalStatus; }
}
