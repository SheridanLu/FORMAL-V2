-- V3.2.0.15__add_company_email.sql
-- 公司邮箱配置字段
ALTER TABLE `sys_dept` ADD COLUMN IF NOT EXISTS `email` VARCHAR(100) DEFAULT '' COMMENT '部门邮箱' AFTER `phone`;
ALTER TABLE `sys_user` ADD COLUMN IF NOT EXISTS `company_email` VARCHAR(100) DEFAULT '' COMMENT '公司邮箱' AFTER `email`;
