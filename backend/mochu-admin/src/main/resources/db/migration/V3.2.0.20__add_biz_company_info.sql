-- V3.0 §3.5: 公司/单位信息表 — 用于合同/供应商表单的甲乙方信息自动填充
CREATE TABLE IF NOT EXISTS `biz_company_info` (
    `id`                INT AUTO_INCREMENT PRIMARY KEY,
    `company_name`      VARCHAR(200) NOT NULL COMMENT '公司/单位名称',
    `credit_code`       VARCHAR(50)  NULL COMMENT '统一社会信用代码',
    `legal_person`      VARCHAR(50)  NULL COMMENT '法定代表人',
    `registered_capital` DECIMAL(16,2) NULL COMMENT '注册资本(万元)',
    `address`           VARCHAR(500) NULL COMMENT '注册地址',
    `contact_person`    VARCHAR(50)  NULL COMMENT '联系人',
    `contact_phone`     VARCHAR(30)  NULL COMMENT '联系电话',
    `bank_name`         VARCHAR(200) NULL COMMENT '开户银行',
    `bank_account`      VARCHAR(50)  NULL COMMENT '银行账号',
    `tax_no`            VARCHAR(50)  NULL COMMENT '税号',
    `remark`            VARCHAR(500) NULL COMMENT '备注',
    `creator_id`        INT          NULL COMMENT '创建人ID',
    `created_at`        DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`           TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY `uk_company_name` (`company_name`, `deleted`),
    INDEX `idx_credit_code` (`credit_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='公司/单位信息';
