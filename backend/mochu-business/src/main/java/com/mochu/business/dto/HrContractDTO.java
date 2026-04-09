package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HrContractDTO {

    private Integer id;

    @NotNull(message = "员工ID不能为空")
    private Integer userId;

    @NotBlank(message = "合同类型不能为空")
    private String contractType;

    @NotNull(message = "合同开始日期不能为空")
    private LocalDate startDate;

    private LocalDate endDate;

    private Integer renewalId;

    private Integer contractFileId;

    /* ---------- 查询条件 ---------- */

    private Integer page;

    private Integer size;

    private String status;
}
