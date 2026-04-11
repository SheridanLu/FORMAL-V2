package com.mochu.business.dto;

import lombok.Data;

@Data
public class SupplierRatingDTO {
    private Integer supplierId;
    private Integer purchaseId;
    private Integer projectId;
    private Integer qualityScore;
    private Integer deliveryScore;
    private Integer serviceScore;
    private Integer priceScore;
    private String commentText;
}
