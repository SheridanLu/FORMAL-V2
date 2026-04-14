-- V3.2.0.16__add_todo_read_at.sql
-- 待办表新增已读时间字段（如果不存在）
-- 注：此字段可能已在 entity 中定义但尚未在数据库中存在
ALTER TABLE `sys_todo` ADD COLUMN IF NOT EXISTS `read_at` DATETIME DEFAULT NULL COMMENT '已读时间' AFTER `status`;
