package com.mochu.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告VO
 */
@Data
public class AnnouncementVO {

    private Integer id;

    private String title;

    private String content;

    private String type;

    private LocalDateTime publishTime;

    private LocalDateTime expireTime;

    private Integer publisherId;

    private String publisherName;

    private String status;

    private Integer isTop;

    private String scope;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
