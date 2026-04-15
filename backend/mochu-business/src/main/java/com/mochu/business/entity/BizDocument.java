package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文档管理实体
 *
 * <p>V3.2: 文档上传、分类、版本追踪
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_document")
public class BizDocument extends BaseEntity {

    /** 文件名 */
    private String fileName;

    /** 文件访问URL */
    private String fileUrl;

    /** 文件存储路径 */
    private String filePath;

    /** 文件大小(字节) */
    private Long fileSize;

    /** MIME类型 */
    private String fileType;

    /** 分类:construction/contract/completion/design/other */
    private String category;

    /** 关联项目ID */
    private Integer projectId;

    /** 上传人ID */
    private Integer uploaderId;

    /** 版本号 */
    private Integer version;

    /** 备注 */
    private String remark;

    // ---------- 联表虚拟字段 ----------

    /** 关联项目名称(联表查询填充) */
    @TableField(exist = false)
    private String projectName;

    /** 上传人姓名(联表查询填充) */
    @TableField(exist = false)
    private String uploaderName;
}
