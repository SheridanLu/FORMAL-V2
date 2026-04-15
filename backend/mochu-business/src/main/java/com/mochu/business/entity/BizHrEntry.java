package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import com.mochu.framework.handler.EncryptedStringTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 入职申请表 — 对照 V3.2 P.59
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "biz_hr_entry", autoResultMap = true)
public class BizHrEntry extends BaseEntity {

    private String entryNo;

    private String applicantName;

    /** 手机号 — AES-256 加密存储 */
    @TableField(typeHandler = EncryptedStringTypeHandler.class)
    private String phone;

    private Integer deptId;

    private String position;

    private LocalDate entryDate;

    private String education;

    private Integer workYears;

    /** 身份证号 — AES-256 加密存储 */
    @TableField(typeHandler = EncryptedStringTypeHandler.class)
    private String idCardNo;

    private String status;
}
