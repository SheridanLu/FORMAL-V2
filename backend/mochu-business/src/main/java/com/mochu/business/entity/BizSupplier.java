package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 供应商表 — 对照 V3.2 P.23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_supplier")
public class BizSupplier extends BaseEntity {

    private String supplierName;

    private String contactPerson;

    private String contactPhone;

    private String address;

    private String bankName;

    private String bankAccount;

    private String taxNo;

    private String status;

    private String remark;
}
