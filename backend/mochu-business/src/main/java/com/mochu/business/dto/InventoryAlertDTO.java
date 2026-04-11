package com.mochu.business.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InventoryAlertDTO {
    private Integer projectId;
    private Integer materialId;
    private String materialName;
    private String unit;
    private BigDecimal safetyQty;
    private BigDecimal minQty;
    private Integer alertEnabled;
}
