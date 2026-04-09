package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HrEntryDTO {

    private Integer id;

    @NotBlank(message = "申请人姓名不能为空")
    private String applicantName;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotNull(message = "入职部门不能为空")
    private Integer deptId;

    private String position;

    @NotNull(message = "入职日期不能为空")
    private LocalDate entryDate;

    private String education;

    private Integer workYears;

    private String idCardNo;

    /* ---------- 查询条件 ---------- */

    private Integer page;

    private Integer size;

    private String status;
}
