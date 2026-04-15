package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 暗项管理实体
 *
 * <p>V3.0: 用于记录施工中发现的未在合同/预算中体现的隐蔽工程项
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_hidden_item")
public class BizHiddenItem extends BaseEntity {

    /** 关联项目ID */
    private Integer projectId;

    /** 关联合同ID */
    private Integer contractId;

    /** 暗项名称 */
    private String itemName;

    /** 类型:material/labor/equipment/other */
    private String itemType;

    /** 数量 */
    private BigDecimal quantity;

    /** 单位 */
    private String unit;

    /** 单价 */
    private BigDecimal unitPrice;

    /** 总金额 */
    private BigDecimal totalAmount;

    /** 预估成本 */
    private BigDecimal estimatedCost;

    /** 状态:identified/quoted/approved/settled */
    private String status;

    /** 发现日期 */
    private LocalDate discoveryDate;

    /** 详细描述 */
    private String description;

    /** 负责人ID */
    private Integer handlerId;

    /** 备注 */
    private String remark;
}
