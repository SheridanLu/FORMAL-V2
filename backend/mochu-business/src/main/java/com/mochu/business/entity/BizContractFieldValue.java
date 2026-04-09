package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 合同字段值表（实例级）
 */
@Data
@TableName("biz_contract_field_value")
public class BizContractFieldValue {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer contractId;
    private String fieldKey;
    private String fieldValue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
