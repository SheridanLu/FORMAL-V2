package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 出库单表 — 对照 V3.2 P.32
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_outbound_order")
public class BizOutboundOrder extends BaseEntity {

    private String outboundNo;

    private Integer projectId;

    private String outboundType;

    private LocalDate outboundDate;

    private String status;

    private String remark;

    @Version
    private Integer version;
}
