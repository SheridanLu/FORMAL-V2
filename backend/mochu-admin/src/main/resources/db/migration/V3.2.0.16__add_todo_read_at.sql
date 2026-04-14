-- V3.2.0.16__add_todo_read_at.sql
-- 待办表新增已读时间字段（兼容 MySQL 8.0，不使用 IF NOT EXISTS）
SET @col_exists = (SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'sys_todo' AND column_name = 'read_at');
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE `sys_todo` ADD COLUMN `read_at` DATETIME DEFAULT NULL COMMENT ''已读时间'' AFTER `status`',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
