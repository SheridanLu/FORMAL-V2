package com.mochu.business.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InventoryCheckDTO {

    private Integer id;

    @NotNull(message = "关联项目不能为空")
    private Integer projectId;

    private LocalDate checkDate;

    private String remark;
}
