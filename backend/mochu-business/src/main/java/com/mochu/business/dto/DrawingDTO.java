package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DrawingDTO {

    private Integer id;

    @NotNull(message = "项目ID不能为空")
    private Integer projectId;

    @NotBlank(message = "图纸名称不能为空")
    private String drawingName;

    private String drawingType;

    private String fileUrl;

    private String version;

    private String remark;
}
