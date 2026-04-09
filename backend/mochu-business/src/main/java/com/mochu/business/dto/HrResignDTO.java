package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HrResignDTO {

    private Integer id;

    @NotNull(message = "离职员工ID不能为空")
    private Integer userId;

    @NotBlank(message = "离职类型不能为空")
    private String resignType;

    @NotNull(message = "离职日期不能为空")
    private LocalDate resignDate;

    private String resignReason;

    private Integer handoverTo;

    /* ---------- 查询条件 ---------- */

    private Integer page;

    private Integer size;

    private String status;

    private String handoverStatus;
}
