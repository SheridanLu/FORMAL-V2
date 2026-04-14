-- V3.2.0.09__add_gantt_progress_desc.sql
-- 甘特图任务新增进度描述字段（兼容 MySQL 8.0，不使用 IF NOT EXISTS）
SET @col_exists = (SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'biz_gantt_task' AND column_name = 'progress_desc');
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE `biz_gantt_task` ADD COLUMN `progress_desc` VARCHAR(500) DEFAULT '''' COMMENT ''进度描述'' AFTER `progress_pct`',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
