-- V3.2.0.03__add_contract_version.sql
-- 合同版本管理表
CREATE TABLE IF NOT EXISTS `biz_contract_version` (
    `id`            INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `contract_id`   INT NOT NULL COMMENT '合同ID',
    `version_no`    INT NOT NULL DEFAULT 1 COMMENT '版本号',
    `snapshot_json` LONGTEXT COMMENT '合同快照JSON',
    `change_reason` VARCHAR(500) DEFAULT '' COMMENT '变更原因',
    `operator_id`   INT COMMENT '操作人ID',
    `created_at`    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_contract_id` (`contract_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
  COMMENT='合同版本历史表';
