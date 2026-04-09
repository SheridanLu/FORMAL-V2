package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 退库单表 — 对照 V3.2 P.34
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_return_order")
public class BizReturnOrder extends BaseEntity {

    private String returnNo;

    private Integer projectId;

    private String disposeMethod;

    private Integer targetProjectId;

    private LocalDate returnDate;

    private String status;

    private String remark;

    @Version
    private Integer version;
}
