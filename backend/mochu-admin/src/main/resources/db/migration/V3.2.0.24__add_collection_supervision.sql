-- V3.0: 回款督办 — 收款计划 + 跟踪记录
CREATE TABLE IF NOT EXISTS `biz_receipt_plan` (
    `id`              INT AUTO_INCREMENT PRIMARY KEY,
    `contract_id`     INT           NOT NULL COMMENT '合同ID',
    `project_id`      INT           NULL COMMENT '项目ID',
    `plan_no`         INT           NOT NULL COMMENT '期次',
    `plan_amount`     DECIMAL(16,2) NOT NULL COMMENT '计划金额',
    `plan_date`       DATE          NOT NULL COMMENT '计划回款日期',
    `actual_amount`   DECIMAL(16,2) NULL COMMENT '实际回款金额',
    `actual_date`     DATE          NULL COMMENT '实际回款日期',
    `status`          VARCHAR(20)   DEFAULT 'pending' COMMENT '状态:pending/partial/completed/overdue',
    `remark`          VARCHAR(500)  NULL COMMENT '备注',
    `creator_id`      INT           NULL,
    `created_at`      DATETIME      DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`         TINYINT       DEFAULT 0,
    INDEX `idx_rp_contract` (`contract_id`),
    INDEX `idx_rp_status_date` (`status`, `plan_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收款计划(回款督办)';

CREATE TABLE IF NOT EXISTS `biz_collection_follow_up` (
    `id`              INT AUTO_INCREMENT PRIMARY KEY,
    `receipt_plan_id` INT           NOT NULL COMMENT '收款计划ID',
    `follow_up_date`  DATE          NOT NULL COMMENT '跟进日期',
    `result`          VARCHAR(500)  NOT NULL COMMENT '跟进结果',
    `next_action`     VARCHAR(500)  NULL COMMENT '下一步计划',
    `handler_id`      INT           NULL COMMENT '跟进人ID',
    `creator_id`      INT           NULL,
    `created_at`      DATETIME      DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`         TINYINT       DEFAULT 0,
    INDEX `idx_cfu_plan` (`receipt_plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='回款跟进记录';
