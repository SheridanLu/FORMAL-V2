package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ChangeOrderDTO {

    private Integer id;

    @NotBlank(message = "变更类型不能为空")
    private String changeType;

    @NotNull(message = "项目ID不能为空")
    private Integer projectId;

    private Integer contractId;

    @NotBlank(message = "变更标题不能为空")
    private String title;

    private String description;

    private BigDecimal totalAmount;

    /** 变更明细列表 */
    private List<ChangeDetailItem> details;

    @Data
    public static class ChangeDetailItem {

        private Integer id;

        private String itemName;

        private String specModel;

        private String unit;

        private BigDecimal planQuantity;

        private BigDecimal actualQuantity;

        private BigDecimal diffQuantity;

        private BigDecimal unitPrice;

        private BigDecimal subtotal;
    }
}
