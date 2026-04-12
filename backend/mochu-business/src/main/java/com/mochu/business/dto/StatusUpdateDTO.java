package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 通用状态更新 DTO（用于所有 PATCH /{id}/status 端点）
 */
@Data
public class StatusUpdateDTO {

    @NotBlank(message = "状态值不能为空")
    private String status;
}
