-- V3.2.0.19__fix_missing_tables_and_index.sql
-- 修复: 创建缺失的表 + biz_salary 新增 gross_salary 列

-- 1. 物料基准价表（BizMaterialBasePrice 实体引用但从未建表）
CREATE TABLE IF NOT EXISTS `biz_material_base_price` (
    `id`              INT          AUTO_INCREMENT PRIMARY KEY,
    `material_id`     INT          NOT NULL COMMENT '物料ID',
    `base_price`      DECIMAL(18,2) DEFAULT 0 COMMENT '基准价',
    `effective_date`  DATE         COMMENT '生效日期',
    `source`          VARCHAR(50)  DEFAULT '' COMMENT '价格来源: manual/contract',
    `source_id`       INT          COMMENT '来源ID(合同ID等)',
    `creator_id`      INT,
    `created_at`      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`         TINYINT      NOT NULL DEFAULT 0,
    INDEX `idx_material_id` (`material_id`),
    INDEX `idx_effective_date` (`effective_date`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  COMMENT = '物料基准价表';

-- 2. 社保配置表（BizSocialInsuranceConfig 实体引用但从未建表）
CREATE TABLE IF NOT EXISTS `biz_social_insurance_config` (
    `id`                          INT          AUTO_INCREMENT PRIMARY KEY,
    `user_id`                     INT          NOT NULL COMMENT '用户ID',
    `pension_base`                DECIMAL(18,2) DEFAULT 0 COMMENT '养老保险基数',
    `pension_personal_rate`       DECIMAL(5,2)  DEFAULT 0 COMMENT '养老个人比例(%)',
    `pension_company_rate`        DECIMAL(5,2)  DEFAULT 0 COMMENT '养老企业比例(%)',
    `medical_base`                DECIMAL(18,2) DEFAULT 0 COMMENT '医疗保险基数',
    `medical_personal_rate`       DECIMAL(5,2)  DEFAULT 0 COMMENT '医疗个人比例(%)',
    `medical_company_rate`        DECIMAL(5,2)  DEFAULT 0 COMMENT '医疗企业比例(%)',
    `unemployment_base`           DECIMAL(18,2) DEFAULT 0 COMMENT '失业保险基数',
    `unemployment_personal_rate`  DECIMAL(5,2)  DEFAULT 0 COMMENT '失业个人比例(%)',
    `unemployment_company_rate`   DECIMAL(5,2)  DEFAULT 0 COMMENT '失业企业比例(%)',
    `housing_base`                DECIMAL(18,2) DEFAULT 0 COMMENT '公积金基数',
    `housing_personal_rate`       DECIMAL(5,2)  DEFAULT 0 COMMENT '公积金个人比例(%)',
    `housing_company_rate`        DECIMAL(5,2)  DEFAULT 0 COMMENT '公积金企业比例(%)',
    `effective_date`              DATE         COMMENT '生效日期',
    `status`                      VARCHAR(20)  DEFAULT 'active' COMMENT 'active/inactive',
    `creator_id`                  INT,
    `created_at`                  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `updated_at`                  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`                     TINYINT      NOT NULL DEFAULT 0,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status`  (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  COMMENT = '社保配置表';

-- 3. biz_salary 新增 gross_salary 列（与 BizSalary 实体对齐）
SET @col_exists = (SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'biz_salary' AND column_name = 'gross_salary');
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE `biz_salary` ADD COLUMN `gross_salary` DECIMAL(18,2) DEFAULT 0 COMMENT ''应发工资(总工资)'' AFTER `allowance`',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
