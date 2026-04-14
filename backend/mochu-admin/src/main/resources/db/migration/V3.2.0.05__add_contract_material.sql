-- V3.2.0.05__add_contract_material.sql
-- 合同物料清单表
CREATE TABLE IF NOT EXISTS `biz_contract_material` (
    `id`            INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `contract_id`   INT NOT NULL COMMENT '合同ID',
    `material_id`   INT COMMENT '物料ID',
    `material_name` VARCHAR(200) DEFAULT '' COMMENT '物料名称',
    `spec_model`    VARCHAR(200) DEFAULT '' COMMENT '规格型号',
    `unit`          VARCHAR(20) DEFAULT '' COMMENT '单位',
    `quantity`      DECIMAL(18,4) DEFAULT 0 COMMENT '数量',
    `unit_price`    DECIMAL(18,2) DEFAULT 0 COMMENT '单价',
    `subtotal`      DECIMAL(18,2) DEFAULT 0 COMMENT '小计',
    `remark`        VARCHAR(255) DEFAULT '',
    `created_at`    DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_contract_id` (`contract_id`),
    INDEX `idx_material_id` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
  COMMENT='合同物料清单表';
