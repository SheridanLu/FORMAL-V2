package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 案例展示表 — 对照 V3.2 P.65
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_case")
public class BizCase extends BaseEntity {

    private Integer projectId;

    private String caseName;

    private String caseType;

    private String summary;

    private String content;

    private Integer coverImageId;

    private Integer displayOrder;

    private String status;
}
