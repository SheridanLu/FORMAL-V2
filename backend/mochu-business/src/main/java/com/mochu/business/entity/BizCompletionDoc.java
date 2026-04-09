package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 竣工资料表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_completion_doc")
public class BizCompletionDoc extends BaseEntity {

    private String docNo;

    private Integer projectId;

    private String docName;

    private String docType;

    private String fileUrl;

    private String remark;

    private String status;
}
