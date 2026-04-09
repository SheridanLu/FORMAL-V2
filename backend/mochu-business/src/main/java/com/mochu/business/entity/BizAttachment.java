package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务附件表 — 对照 V3.2 P.70
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_attachment")
public class BizAttachment extends BaseEntity {

    private String fileName;

    private String filePath;

    private Long fileSize;

    private String fileType;

    private String fileExt;

    private String md5;

    private String bizType;

    private Integer bizId;

    private Integer status;
}
