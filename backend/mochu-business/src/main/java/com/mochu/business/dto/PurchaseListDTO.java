package com.mochu.business.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PurchaseListDTO {

    private Integer id;

    @NotNull(message = "关联项目不能为空")
    private Integer projectId;

    private BigDecimal totalAmount;

    private String remark;

    @Valid
    private List<ItemDTO> items;

    @Data
    public static class ItemDTO {

        private Integer id;

        private Integer materialId;

        private String materialName;

        private String specModel;

        private String unit;

        @NotNull(message = "需求数量不能为空")
        private BigDecimal quantity;

        private BigDecimal estimatedPrice;

        private BigDecimal subtotal;

        private String remark;
    }
}
