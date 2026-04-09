package com.mochu.system.dto;

import lombok.Data;

/**
 * 公告查询DTO
 */
@Data
public class AnnouncementQueryDTO {

    private String title;

    private String type;

    /** draft/published/offline/expired */
    private String status;

    private Integer page;

    private Integer size;
}
