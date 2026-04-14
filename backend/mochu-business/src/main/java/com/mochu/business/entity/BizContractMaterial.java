package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("biz_contract_material")
public class BizContractMaterial {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer contractId;
    private Integer materialId;
    private String materialName;
    private String spec;
    private String unit;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private String remark;
    private LocalDateTime createdAt;
}
