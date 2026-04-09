package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 竣工图纸表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_drawing")
public class BizDrawing extends BaseEntity {

    private String drawingNo;

    private Integer projectId;

    private String drawingName;

    private String drawingType;

    private String fileUrl;

    private String version;

    private String remark;

    private String status;
}
