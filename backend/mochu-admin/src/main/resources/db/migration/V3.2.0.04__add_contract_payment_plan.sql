-- V3.2.0.04__add_contract_payment_plan.sql
-- 合同付款计划表
CREATE TABLE IF NOT EXISTS `biz_contract_payment_plan` (
    `id`            INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `contract_id`   INT NOT NULL COMMENT '合同ID',
    `plan_no`       INT NOT NULL DEFAULT 1 COMMENT '期数',
    `plan_amount`   DECIMAL(18,2) NOT NULL DEFAULT 0 COMMENT '计划付款金额',
    `plan_date`     DATE COMMENT '计划付款日期',
    `condition_desc` VARCHAR(500) DEFAULT '' COMMENT '付款条件说明',
    `actual_amount` DECIMAL(18,2) DEFAULT 0 COMMENT '实际付款金额',
    `actual_date`   DATE COMMENT '实际付款日期',
    `status`        VARCHAR(20) DEFAULT 'pending' COMMENT 'pending/paid/overdue',
    `remark`        TEXT,
    `creator_id`    INT,
    `created_at`    DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`       TINYINT DEFAULT 0,
    INDEX `idx_contract_id` (`contract_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
  COMMENT='合同付款计划表';
