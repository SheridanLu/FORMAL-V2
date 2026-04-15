package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 广联达数据导入记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_glodon_import")
public class BizGlodonImport extends BaseEntity {

    private Integer projectId;
    private String fileName;
    private String fileUrl;
    /** cost/quantity/price */
    private String importType;
    /** pending/processing/success/failed */
    private String status;
    private Integer totalRows;
    private Integer successRows;
    private String errorMsg;
}
