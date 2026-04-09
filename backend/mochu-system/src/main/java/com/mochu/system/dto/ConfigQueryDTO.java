package com.mochu.system.dto;

import lombok.Data;

@Data
public class ConfigQueryDTO {

    private String configKey;

    private String configGroup;

    private Integer status;

    private Integer page;

    private Integer size;
}
