package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpotPurchaseDTO {

    private Integer id;

    @NotNull(message = "关联项目不能为空")
    private Integer projectId;

    @NotBlank(message = "采购物品名称不能为空")
    private String itemName;

    private String specModel;

    @NotNull(message = "采购数量不能为空")
    private BigDecimal quantity;

    @NotNull(message = "单价不能为空")
    private BigDecimal unitPrice;

    private BigDecimal amount;

    private String supplierName;

    private String remark;
}
