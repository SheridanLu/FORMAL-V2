-- V3.2.0.08__add_inventory_record.sql
-- 库存变动记录表
CREATE TABLE IF NOT EXISTS `biz_inventory_record` (
    `id`            INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `project_id`    INT NOT NULL COMMENT '项目ID',
    `material_id`   INT NOT NULL COMMENT '物料ID',
    `record_type`   VARCHAR(20) NOT NULL COMMENT 'inbound/outbound/return/check/transfer',
    `quantity`      DECIMAL(18,4) NOT NULL COMMENT '变动数量（正入负出）',
    `before_qty`    DECIMAL(18,4) DEFAULT 0 COMMENT '变动前数量',
    `after_qty`     DECIMAL(18,4) DEFAULT 0 COMMENT '变动后数量',
    `biz_type`      VARCHAR(50) DEFAULT '' COMMENT '关联业务类型',
    `biz_id`        INT COMMENT '关联业务ID',
    `remark`        VARCHAR(255) DEFAULT '',
    `creator_id`    INT,
    `created_at`    DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_project_id` (`project_id`),
    INDEX `idx_material_id` (`material_id`),
    INDEX `idx_record_type` (`record_type`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
  COMMENT='库存变动记录表';
