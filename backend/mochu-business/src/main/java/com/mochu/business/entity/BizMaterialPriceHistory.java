package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("biz_material_price_history")
public class BizMaterialPriceHistory {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer materialId;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private BigDecimal changeRate;
    private String changeReason;
    private String source;
    private Integer sourceId;
    private Integer creatorId;
    private LocalDateTime createdAt;
}
