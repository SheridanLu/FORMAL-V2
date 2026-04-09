package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 收入拆分
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_income_split")
public class BizIncomeSplit extends BaseEntity {

    private String splitNo;

    private Integer projectId;

    private Integer contractId;

    private String splitItem;

    private BigDecimal amount;

    private BigDecimal ratio;

    private String remark;

    /** draft/pending/approved/rejected */
    private String status;
}
