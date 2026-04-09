package com.mochu.business.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MaterialBatchDTO {

    @NotNull(message = "items不能为空")
    @Size(min = 1, max = 100, message = "单次批量创建1~100条")
    @Valid
    private List<MaterialBatchItem> items;

    @Data
    public static class MaterialBatchItem {

        @NotBlank(message = "材料名称不能为空")
        @Size(min = 2, max = 100, message = "材料名称长度2~100字")
        private String materialName;

        @Size(max = 200, message = "规格型号长度不超过200字")
        private String specModel;

        @NotBlank(message = "分类不能为空")
        private String category;

        @NotBlank(message = "单位不能为空")
        private String unit;

        @NotNull(message = "含税基准价不能为空")
        @DecimalMin(value = "0", message = "含税基准价不能为负数")
        private BigDecimal basePriceWithTax;

        @NotNull(message = "税率不能为空")
        private Integer taxRate;
    }
}
