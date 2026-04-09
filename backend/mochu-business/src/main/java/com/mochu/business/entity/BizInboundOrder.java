package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 入库单表 — 对照 V3.2 P.30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_inbound_order")
public class BizInboundOrder extends BaseEntity {

    private String inboundNo;

    private Integer contractId;

    private Integer projectId;

    private String warehouse;

    private LocalDate inboundDate;

    private String status;

    private String remark;

    @Version
    private Integer version;
}
