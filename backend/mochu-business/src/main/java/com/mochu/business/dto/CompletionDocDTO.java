package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompletionDocDTO {

    private Integer id;

    @NotNull(message = "项目ID不能为空")
    private Integer projectId;

    @NotBlank(message = "资料名称不能为空")
    private String docName;

    private String docType;

    private String fileUrl;

    private String remark;
}
