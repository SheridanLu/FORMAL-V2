-- V3.2.0.10__add_income_split_item.sql
-- 收入分成明细表
CREATE TABLE IF NOT EXISTS `biz_income_split_item` (
    `id`            INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `split_id`      INT NOT NULL COMMENT '分成单ID',
    `dept_id`       INT COMMENT '部门ID',
    `user_id`       INT COMMENT '人员ID',
    `split_ratio`   DECIMAL(5,4) DEFAULT 0 COMMENT '分成比例',
    `split_amount`  DECIMAL(18,2) DEFAULT 0 COMMENT '分成金额',
    `remark`        VARCHAR(255) DEFAULT '',
    `created_at`    DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_split_id` (`split_id`),
    INDEX `idx_dept_id` (`dept_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
  COMMENT='收入分成明细表';
