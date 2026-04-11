package com.mochu.business.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 审批转办/加签 DTO
 */
@Data
public class ApprovalTransferDTO {

    /** 目标用户ID */
    @NotNull(message = "目标用户ID不能为空")
    private Integer targetUserId;

    /** 审批意见 */
    private String opinion;
}
