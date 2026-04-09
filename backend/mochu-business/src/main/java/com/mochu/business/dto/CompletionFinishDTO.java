package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CompletionFinishDTO {

    private Integer id;

    @NotNull(message = "项目ID不能为空")
    private Integer projectId;

    @NotBlank(message = "验收标题不能为空")
    private String title;

    private LocalDate planFinishDate;

    private String finishContent;

    private String selfCheckResult;

    private String remainingIssues;
}
