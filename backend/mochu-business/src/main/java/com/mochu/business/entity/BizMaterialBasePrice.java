package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_material_base_price")
public class BizMaterialBasePrice extends BaseEntity {
    private Integer materialId;
    private BigDecimal basePrice;
    private LocalDate effectiveDate;
    private String source;      // manual/contract
    private Integer sourceId;
}
