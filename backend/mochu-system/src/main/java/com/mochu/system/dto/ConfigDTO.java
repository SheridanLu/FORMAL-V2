package com.mochu.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfigDTO {

    private Integer id;

    @NotBlank(message = "配置键不能为空")
    private String configKey;

    @NotBlank(message = "配置值不能为空")
    private String configValue;

    private String configDesc;

    private String configGroup;

    private Integer status;
}
