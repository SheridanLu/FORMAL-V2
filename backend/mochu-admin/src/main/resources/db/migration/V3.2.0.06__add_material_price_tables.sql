-- V3.2.0.06__add_material_price_tables.sql
-- 物料价格历史表
CREATE TABLE IF NOT EXISTS `biz_material_price_history` (
    `id`            INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `material_id`   INT NOT NULL COMMENT '物料ID',
    `supplier_id`   INT COMMENT '供应商ID',
    `price`         DECIMAL(18,2) NOT NULL COMMENT '价格',
    `tax_rate`      DECIMAL(5,2) DEFAULT 0 COMMENT '税率',
    `price_date`    DATE COMMENT '报价日期',
    `source`        VARCHAR(50) DEFAULT '' COMMENT '价格来源',
    `remark`        VARCHAR(255) DEFAULT '',
    `creator_id`    INT,
    `created_at`    DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_material_id` (`material_id`),
    INDEX `idx_supplier_id` (`supplier_id`),
    INDEX `idx_price_date` (`price_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
  COMMENT='物料价格历史表';

-- 物料价格对比表
CREATE TABLE IF NOT EXISTS `biz_material_price_compare` (
    `id`            INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `compare_no`    VARCHAR(50) NOT NULL COMMENT '比价单号',
    `material_id`   INT NOT NULL COMMENT '物料ID',
    `project_id`    INT COMMENT '项目ID',
    `result_supplier_id` INT COMMENT '中标供应商ID',
    `status`        VARCHAR(20) DEFAULT 'draft' COMMENT 'draft/completed',
    `remark`        TEXT,
    `creator_id`    INT,
    `created_at`    DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`       TINYINT DEFAULT 0,
    INDEX `idx_compare_no` (`compare_no`),
    INDEX `idx_material_id` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
  COMMENT='物料价格对比表';
