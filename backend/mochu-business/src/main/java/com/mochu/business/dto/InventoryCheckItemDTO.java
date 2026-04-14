package com.mochu.business.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class InventoryCheckItemDTO {
    private Integer id;
    private BigDecimal actualQuantity;
    private String diffReason;
}
