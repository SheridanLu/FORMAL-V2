package com.mochu.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 首页轮播图实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_banner")
public class SysBanner extends BaseEntity {

    /** 标题 */
    private String title;

    /** 图片URL */
    private String imageUrl;

    /** 点击跳转链接 */
    private String linkUrl;

    /** 排序(越大越靠前) */
    private Integer sortOrder;

    /** 状态:1启用 0禁用 */
    private Integer status;

    /** 展示开始时间 */
    private LocalDateTime startTime;

    /** 展示结束时间 */
    private LocalDateTime endTime;

    /** 备注 */
    private String remark;
}
