package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_supplier_rating")
public class BizSupplierRating extends BaseEntity {
    private Integer supplierId;
    private Integer purchaseId;
    private Integer projectId;
    private Integer qualityScore;
    private Integer deliveryScore;
    private Integer serviceScore;
    private Integer priceScore;
    private BigDecimal totalScore;
    private String commentText;
}
