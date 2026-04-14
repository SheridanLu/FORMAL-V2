-- V3.2.0.02__add_password_changed_at.sql
-- 用户表新增密码修改时间字段（兼容 MySQL 8.0，不使用 IF NOT EXISTS）
SET @col_exists = (SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'sys_user' AND column_name = 'password_changed_at');
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE `sys_user` ADD COLUMN `password_changed_at` DATETIME DEFAULT NULL COMMENT ''密码最后修改时间'' AFTER `force_change_pwd`',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
