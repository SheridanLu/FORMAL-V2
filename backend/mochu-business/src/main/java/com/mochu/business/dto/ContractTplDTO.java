package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 合同模板 DTO
 */
@Data
public class ContractTplDTO {

    @NotBlank(message = "合同类型不能为空")
    private String contractType;

    @NotBlank(message = "模板名称不能为空")
    private String tplName;

    private String description;
    private Integer status;
}
