package com.mochu.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告创建/编辑DTO
 */
@Data
public class AnnouncementDTO {

    private Integer id;

    @NotBlank(message = "公告标题不能为空")
    private String title;

    @NotBlank(message = "公告内容不能为空")
    private String content;

    @NotBlank(message = "公告类型不能为空")
    private String type;

    private LocalDateTime expireTime;

    /** 定时发布时间（为空则手动发布） */
    private LocalDateTime scheduledPublishTime;

    private Integer isTop;

    /** 可见范围,all或逗号分隔部门ID */
    private String scope;
}
