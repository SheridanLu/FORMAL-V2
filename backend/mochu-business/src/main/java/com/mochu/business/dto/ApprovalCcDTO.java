package com.mochu.business.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 审批抄送 DTO
 */
@Data
public class ApprovalCcDTO {

    /** 抄送用户ID列表 */
    @NotEmpty(message = "抄送用户列表不能为空")
    private List<Integer> userIds;
}
