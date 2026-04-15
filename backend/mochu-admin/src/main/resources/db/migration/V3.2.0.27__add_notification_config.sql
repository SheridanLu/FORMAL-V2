-- V3.2.0.27: 用户通知偏好 + 审计日志分区准备
-- 1. 用户通知偏好表
CREATE TABLE IF NOT EXISTS `sys_user_notification_config` (
    `id`              INT          NOT NULL AUTO_INCREMENT,
    `user_id`         INT          NOT NULL           COMMENT '用户ID',
    `channel_station` TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '站内信(始终开启)',
    `channel_sms`     TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '短信通知',
    `channel_email`   TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '邮件通知',
    `channel_wechat`  TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '企业微信通知',
    `quiet_start`     TIME         NULL               COMMENT '免打扰开始时间',
    `quiet_end`       TIME         NULL               COMMENT '免打扰结束时间',
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户通知偏好';

-- 2. 审计日志归档表索引优化（如不存在则创建）
CREATE TABLE IF NOT EXISTS `sys_audit_log_archive` LIKE `sys_audit_log`;
