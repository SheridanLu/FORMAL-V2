package com.mochu.system.vo;

import lombok.Data;

import java.util.List;

/**
 * 首页数据 VO — 对照 V3.2 §4.2
 */
@Data
public class HomeVO {

    /** 待办数量 */
    private Integer todoCount;

    /** 公告列表（最新5条） */
    private List<AnnouncementVO> announcements;

    /** 快捷入口 */
    private List<ShortcutVO> shortcuts;

    @Data
    public static class AnnouncementVO {
        private Integer id;
        private String title;
        private String publishTime;
    }

    @Data
    public static class ShortcutVO {
        private String funcCode;
        private String funcName;
        private String funcIcon;
        private Integer sortOrder;
    }
}
