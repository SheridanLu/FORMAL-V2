package com.mochu.business.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InboundOrderDTO {

    private Integer id;

    @NotNull(message = "关联合同不能为空")
    private Integer contractId;

    @NotNull(message = "关联项目不能为空")
    private Integer projectId;

    private String warehouse;

    private LocalDate inboundDate;

    private String remark;
}
