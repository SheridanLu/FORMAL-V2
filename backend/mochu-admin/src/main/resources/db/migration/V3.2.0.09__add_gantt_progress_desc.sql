-- V3.2.0.09__add_gantt_progress_desc.sql
-- 甘特图任务新增进度描述字段
ALTER TABLE `biz_gantt_task` ADD COLUMN IF NOT EXISTS `progress_desc` VARCHAR(500) DEFAULT '' COMMENT '进度描述' AFTER `progress_pct`;
