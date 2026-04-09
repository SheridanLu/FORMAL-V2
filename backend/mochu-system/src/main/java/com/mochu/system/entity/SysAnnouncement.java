package com.mochu.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统公告表 — 对照 V3.2 附录P.13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_announcement")
public class SysAnnouncement extends BaseEntity {

    /** 公告标题 */
    private String title;

    /** 公告内容(富文本HTML) */
    private String content;

    /** 公告类型:notice/policy/activity */
    private String type;

    /** 发布时间 */
    private LocalDateTime publishTime;

    /** 过期时间 */
    private LocalDateTime expireTime;

    /** 发布人ID */
    private Integer publisherId;

    /** draft/published/offline/expired */
    private String status;

    /** 是否置顶 */
    private Integer isTop;

    /** 可见范围,all或逗号分隔部门ID */
    private String scope;
}
