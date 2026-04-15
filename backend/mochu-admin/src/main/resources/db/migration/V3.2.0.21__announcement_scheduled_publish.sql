-- V3.0: 公告定时发布支持
ALTER TABLE `sys_announcement`
    ADD COLUMN `scheduled_publish_time` DATETIME NULL COMMENT '定时发布时间' AFTER `expire_time`;

-- 索引: 定时发布任务扫描
CREATE INDEX `idx_ann_scheduled` ON `sys_announcement` (`status`, `scheduled_publish_time`);
