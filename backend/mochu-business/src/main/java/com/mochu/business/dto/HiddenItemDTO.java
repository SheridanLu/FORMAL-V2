package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 暗项 DTO
 */
@Data
public class HiddenItemDTO {

    @NotNull(message = "请选择关联项目")
    private Integer projectId;

    private Integer contractId;

    @NotBlank(message = "请输入暗项名称")
    private String itemName;

    private String itemType;

    private BigDecimal quantity;

    private String unit;

    private BigDecimal unitPrice;

    private BigDecimal totalAmount;

    private BigDecimal estimatedCost;

    private LocalDate discoveryDate;

    private String description;

    private Integer handlerId;

    private String remark;
}
