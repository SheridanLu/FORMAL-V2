package com.mochu.business.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InventoryTransferDTO {
    private Integer fromProjectId;
    private Integer toProjectId;
    private Integer materialId;
    private BigDecimal qty;
    private String remark;
}
