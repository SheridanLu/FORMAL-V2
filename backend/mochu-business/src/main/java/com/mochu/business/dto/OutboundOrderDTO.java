package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OutboundOrderDTO {

    private Integer id;

    @NotNull(message = "关联项目不能为空")
    private Integer projectId;

    @NotBlank(message = "出库类型不能为空")
    private String outboundType;

    private LocalDate outboundDate;

    private String remark;
}
