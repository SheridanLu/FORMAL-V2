package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 审批流程定义 DTO
 */
@Data
public class FlowDefDTO {

    private Integer id;

    @NotBlank(message = "业务类型不能为空")
    private String bizType;

    @NotBlank(message = "流程名称不能为空")
    private String flowName;

    @NotNull(message = "审批节点配置不能为空")
    private String nodesJson;

    private String conditionJson;

    private Integer status;

    private Integer version;
}
