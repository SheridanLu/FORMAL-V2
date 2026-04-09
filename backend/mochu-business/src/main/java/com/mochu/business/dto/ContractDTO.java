package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
public class ContractDTO {

    private Integer id;

    @NotBlank(message = "合同名称不能为空")
    private String contractName;

    @NotBlank(message = "合同类型不能为空")
    private String contractType;

    @NotNull(message = "关联项目不能为空")
    private Integer projectId;

    private Integer supplierId;

    @NotNull(message = "含税金额不能为空")
    private BigDecimal amountWithTax;

    @NotNull(message = "不含税金额不能为空")
    private BigDecimal amountWithoutTax;

    @NotNull(message = "税率不能为空")
    private BigDecimal taxRate;

    private BigDecimal taxAmount;

    private LocalDate signDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String partyA;
    private String partyB;
    private Integer parentContractId;
    private String remark;

    /** 模板字段填写值: {fieldKey: fieldValue} */
    private Map<String, String> fieldValues;
}
