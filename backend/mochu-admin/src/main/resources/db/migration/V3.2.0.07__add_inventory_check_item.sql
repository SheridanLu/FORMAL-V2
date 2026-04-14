-- V3.2.0.07__add_inventory_check_item.sql
-- 盘点明细表
CREATE TABLE IF NOT EXISTS `biz_inventory_check_item` (
    `id`                INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `check_id`          INT NOT NULL COMMENT '盘点单ID',
    `material_id`       INT NOT NULL COMMENT '物料ID',
    `material_name`     VARCHAR(200) DEFAULT '' COMMENT '物料名称',
    `unit`              VARCHAR(20) DEFAULT '' COMMENT '单位',
    `book_quantity`     DECIMAL(18,4) DEFAULT 0 COMMENT '账面数量',
    `actual_quantity`   DECIMAL(18,4) DEFAULT 0 COMMENT '实盘数量',
    `diff_quantity`     DECIMAL(18,4) DEFAULT 0 COMMENT '差异数量',
    `diff_reason`       VARCHAR(255) DEFAULT '' COMMENT '差异原因',
    `created_at`        DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_check_id` (`check_id`),
    INDEX `idx_material_id` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
  COMMENT='盘点明细表';
