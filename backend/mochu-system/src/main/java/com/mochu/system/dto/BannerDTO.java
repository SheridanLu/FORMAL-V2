package com.mochu.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 轮播图 DTO
 */
@Data
public class BannerDTO {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题最长200字符")
    private String title;

    @NotBlank(message = "图片URL不能为空")
    @Size(max = 500, message = "图片URL最长500字符")
    private String imageUrl;

    @Size(max = 500, message = "跳转链接最长500字符")
    private String linkUrl;

    private Integer sortOrder;

    private Integer status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Size(max = 500, message = "备注最长500字符")
    private String remark;
}
