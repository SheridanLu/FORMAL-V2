package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CaseDTO {

    private Integer id;

    private Integer projectId;

    @NotBlank(message = "案例名称不能为空")
    private String caseName;

    private String caseType;

    private String summary;

    private String content;

    private Integer coverImageId;

    private Integer displayOrder;
}
