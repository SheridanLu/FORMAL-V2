-- V3.2.0.13__add_sms_code.sql
-- 短信验证码表
CREATE TABLE IF NOT EXISTS `sys_sms_code` (
    `id`            INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `phone`         VARCHAR(20) NOT NULL COMMENT '手机号',
    `code`          VARCHAR(10) NOT NULL COMMENT '验证码',
    `biz_type`      VARCHAR(50) DEFAULT 'login' COMMENT '业务类型 login/reset/bind',
    `status`        TINYINT DEFAULT 0 COMMENT '0未使用 1已使用 2已过期',
    `expire_at`     DATETIME NOT NULL COMMENT '过期时间',
    `ip_address`    VARCHAR(50) DEFAULT '' COMMENT '请求IP',
    `created_at`    DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_phone` (`phone`),
    INDEX `idx_expire_at` (`expire_at`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
  COMMENT='短信验证码表';
