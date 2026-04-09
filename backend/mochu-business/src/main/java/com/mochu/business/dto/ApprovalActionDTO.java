package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 审批操作 DTO（提交/审批/驳回）
 */
@Data
public class ApprovalActionDTO {

    /** 审批实例ID（审批/驳回时必填） */
    private Integer instanceId;

    /** 业务类型（提交时必填） */
    @NotBlank(message = "业务类型不能为空")
    private String bizType;

    /** 业务单据ID（提交时必填） */
    @NotNull(message = "业务单据ID不能为空")
    private Integer bizId;

    /** 操作: submit / approve / reject */
    @NotBlank(message = "操作类型不能为空")
    private String action;

    /** 审批意见 */
    private String opinion;
}
