package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CollectionFollowUpDTO {
    @NotNull(message = "收款计划ID不能为空")
    private Integer receiptPlanId;
    @NotNull(message = "跟进日期不能为空")
    private LocalDate followUpDate;
    @NotBlank(message = "跟进结果不能为空")
    private String result;
    private String nextAction;
}
