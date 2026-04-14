-- V3.2.0.11__add_payment_inbound_rel.sql
-- 付款申请与入库单关联表
CREATE TABLE IF NOT EXISTS `biz_payment_inbound_rel` (
    `id`            INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `payment_id`    INT NOT NULL COMMENT '付款申请ID',
    `inbound_id`    INT NOT NULL COMMENT '入库单ID',
    `amount`        DECIMAL(18,2) DEFAULT 0 COMMENT '关联金额',
    `created_at`    DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_payment_id` (`payment_id`),
    INDEX `idx_inbound_id` (`inbound_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
  COMMENT='付款申请与入库单关联表';
