package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExceptionTaskDTO {

    private Integer id;

    @NotBlank(message = "业务类型不能为空")
    private String bizType;

    @NotNull(message = "业务单据ID不能为空")
    private Integer bizId;

    @NotBlank(message = "失败原因不能为空")
    private String failReason;

    private Integer handlerId;

    private String resolveRemark;
}
