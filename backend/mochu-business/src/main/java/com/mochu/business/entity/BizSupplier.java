package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import com.mochu.framework.handler.EncryptedStringTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 供应商表 — 对照 V3.2 P.23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "biz_supplier", autoResultMap = true)
public class BizSupplier extends BaseEntity {

    private String supplierName;

    private String contactPerson;

    /** 联系电话 — AES-256 加密存储 */
    @TableField(typeHandler = EncryptedStringTypeHandler.class)
    private String contactPhone;

    private String address;

    private String bankName;

    /** 银行账号 — AES-256 加密存储 */
    @TableField(typeHandler = EncryptedStringTypeHandler.class)
    private String bankAccount;

    /** 税号 — AES-256 加密存储 */
    @TableField(typeHandler = EncryptedStringTypeHandler.class)
    private String taxNo;

    private String status;

    private String remark;
}
