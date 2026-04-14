-- V3.2.0.02__add_password_changed_at.sql
-- 用户表新增密码修改时间字段
ALTER TABLE `sys_user` ADD COLUMN IF NOT EXISTS `password_changed_at` DATETIME DEFAULT NULL COMMENT '密码最后修改时间' AFTER `force_change_pwd`;
