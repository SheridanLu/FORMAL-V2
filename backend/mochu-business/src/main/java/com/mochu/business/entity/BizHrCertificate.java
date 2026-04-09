package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 资质表 — 对照 V3.2 P.58
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_hr_certificate")
public class BizHrCertificate extends BaseEntity {

    private String certType;

    private Integer userId;

    private String certName;

    private String certCategory;

    private String certNo;

    private LocalDate issueDate;

    private LocalDate expireDate;

    private Integer attachmentId;

    private String warnStatus;

    private String status;
}
