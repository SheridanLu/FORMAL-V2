-- 用户表新增密码修改时间字段
ALTER TABLE `sys_user` ADD COLUMN `password_changed_at` DATETIME DEFAULT NULL
    COMMENT '密码最后修改时间' AFTER `lock_until`;
-- 初始化已有用户
UPDATE `sys_user` SET `password_changed_at` = `created_at`
    WHERE `password_changed_at` IS NULL;
