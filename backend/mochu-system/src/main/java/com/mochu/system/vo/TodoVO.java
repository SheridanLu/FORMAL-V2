package com.mochu.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoVO {

    private Integer id;

    private Integer userId;

    private String bizType;

    private Integer bizId;

    private String title;

    private String content;

    /** 0待处理/1已处理 */
    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
