package com.mochu.business.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class IncomeSplitDTO {

    private Integer id;

    @NotNull(message = "项目ID不能为空")
    private Integer projectId;

    private Integer contractId;

    @NotNull(message = "拆分项不能为空")
    private String splitItem;

    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    private BigDecimal ratio;

    private String remark;
}
