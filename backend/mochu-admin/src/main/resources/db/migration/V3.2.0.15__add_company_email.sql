-- V3.2.0.15__add_company_email.sql
-- 公司邮箱配置字段（兼容 MySQL 8.0，不使用 IF NOT EXISTS）

-- sys_dept.email
SET @col_exists = (SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'sys_dept' AND column_name = 'email');
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE `sys_dept` ADD COLUMN `email` VARCHAR(100) DEFAULT '''' COMMENT ''部门邮箱'' AFTER `phone`',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- sys_user.company_email
SET @col_exists = (SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'sys_user' AND column_name = 'company_email');
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE `sys_user` ADD COLUMN `company_email` VARCHAR(100) DEFAULT '''' COMMENT ''公司邮箱'' AFTER `email`',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
