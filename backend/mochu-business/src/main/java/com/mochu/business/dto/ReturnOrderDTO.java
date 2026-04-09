package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReturnOrderDTO {

    private Integer id;

    @NotNull(message = "关联项目不能为空")
    private Integer projectId;

    @NotBlank(message = "处置方式不能为空")
    private String disposeMethod;

    private Integer targetProjectId;

    private LocalDate returnDate;

    private String remark;
}
