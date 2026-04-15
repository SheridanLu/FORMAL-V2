package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 文档管理 DTO — 用于手动创建/更新文档记录
 */
@Data
public class DocumentDTO {

    @NotBlank(message = "文件名不能为空")
    @Size(max = 500, message = "文件名最长500字符")
    private String fileName;

    @Size(max = 1000, message = "文件URL最长1000字符")
    private String fileUrl;

    @Size(max = 1000, message = "文件路径最长1000字符")
    private String filePath;

    private Long fileSize;

    @Size(max = 100, message = "文件类型最长100字符")
    private String fileType;

    @Size(max = 50, message = "分类最长50字符")
    private String category;

    private Integer projectId;

    @Size(max = 500, message = "备注最长500字符")
    private String remark;
}
