-- ============================================================
-- P1 缺失表 DDL（共16张，sys_role_data_scope 已在 P0 中创建）
-- V3.2.0.03
-- ============================================================

-- 2. sms_code — 短信验证码
CREATE TABLE IF NOT EXISTS `sms_code` (
    `id`         INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `phone`      VARCHAR(20) NOT NULL COMMENT '手机号',
    `code`       VARCHAR(6) NOT NULL COMMENT '验证码',
    `used`       TINYINT DEFAULT 0 COMMENT '是否已使用: 0未使用 1已使用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `expired_at` DATETIME NOT NULL COMMENT '过期时间',
    INDEX `idx_phone` (`phone`),
    INDEX `idx_expired_at` (`expired_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='短信验证码表';

-- 3. company_email — 企业邮箱
CREATE TABLE IF NOT EXISTS `company_email` (
    `id`         INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `user_id`    INT NOT NULL COMMENT '用户ID',
    `email`      VARCHAR(100) NOT NULL COMMENT '企业邮箱地址',
    `status`     TINYINT DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_user_id` (`user_id`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='企业邮箱表';

-- 4. sys_user_shortcut — 用户快捷入口
CREATE TABLE IF NOT EXISTS `sys_user_shortcut` (
    `id`         INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `user_id`    INT NOT NULL COMMENT '用户ID',
    `menu_code`  VARCHAR(50) NOT NULL COMMENT '菜单编码',
    `menu_name`  VARCHAR(50) NOT NULL COMMENT '菜单名称',
    `menu_icon`  VARCHAR(50) DEFAULT NULL COMMENT '菜单图标',
    `menu_path`  VARCHAR(200) DEFAULT NULL COMMENT '菜单路径',
    `sort_order` INT DEFAULT 0 COMMENT '排序序号',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_user_menu` (`user_id`, `menu_code`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户快捷入口表';

-- 5. sys_user_config — 用户个性化配置
CREATE TABLE IF NOT EXISTS `sys_user_config` (
    `id`           INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `user_id`      INT NOT NULL COMMENT '用户ID',
    `config_key`   VARCHAR(50) NOT NULL COMMENT '配置键',
    `config_value` VARCHAR(500) DEFAULT NULL COMMENT '配置值',
    `updated_at`   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_user_key` (`user_id`, `config_key`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户个性化配置表';

-- 6. biz_contract_version — 合同版本快照
CREATE TABLE IF NOT EXISTS `biz_contract_version` (
    `id`              INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `contract_id`     INT NOT NULL COMMENT '合同ID',
    `version_no`      INT NOT NULL COMMENT '版本号(从1递增)',
    `snapshot_json`   JSON NOT NULL COMMENT '合同全量快照(JSON)',
    `change_summary`  TEXT DEFAULT NULL COMMENT '变更摘要',
    `change_type`     VARCHAR(30) DEFAULT NULL COMMENT '变更类型: supplement补充协议/terminate终止/amend修订',
    `creator_id`      INT DEFAULT NULL COMMENT '操作人ID',
    `created_at`      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_contract_id` (`contract_id`),
    UNIQUE KEY `uk_contract_version` (`contract_id`, `version_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='合同版本快照表';

-- 7. biz_contract_payment_plan — 合同付款计划
CREATE TABLE IF NOT EXISTS `biz_contract_payment_plan` (
    `id`            INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `contract_id`   INT NOT NULL COMMENT '合同ID',
    `plan_no`       INT NOT NULL COMMENT '计划序号',
    `plan_name`     VARCHAR(100) DEFAULT NULL COMMENT '计划名称',
    `plan_amount`   DECIMAL(14,2) NOT NULL COMMENT '计划付款金额',
    `plan_date`     DATE DEFAULT NULL COMMENT '计划付款日期',
    `actual_amount` DECIMAL(14,2) DEFAULT NULL COMMENT '实际付款金额',
    `actual_date`   DATE DEFAULT NULL COMMENT '实际付款日期',
    `status`        VARCHAR(30) DEFAULT 'pending' COMMENT '状态: pending待付/paid已付/overdue逾期',
    `remark`        VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `creator_id`    INT DEFAULT NULL COMMENT '创建人ID',
    `created_at`    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT DEFAULT 0 COMMENT '逻辑删除: 0正常 1删除',
    INDEX `idx_contract_id` (`contract_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='合同付款计划表';

-- 8. biz_contract_material — 合同材料明细
CREATE TABLE IF NOT EXISTS `biz_contract_material` (
    `id`            INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `contract_id`   INT NOT NULL COMMENT '合同ID',
    `material_id`   INT DEFAULT NULL COMMENT '材料ID(关联biz_material_base)',
    `material_name` VARCHAR(100) NOT NULL COMMENT '材料名称',
    `spec`          VARCHAR(100) DEFAULT NULL COMMENT '规格型号',
    `unit`          VARCHAR(20) DEFAULT NULL COMMENT '单位',
    `quantity`      DECIMAL(14,4) NOT NULL COMMENT '数量',
    `unit_price`    DECIMAL(14,2) NOT NULL COMMENT '单价(含税)',
    `subtotal`      DECIMAL(14,2) NOT NULL COMMENT '小计金额',
    `remark`        VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `created_at`    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_contract_id` (`contract_id`),
    INDEX `idx_material_id` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='合同材料明细表';

-- 9. biz_material_base_price — 材料基准价
CREATE TABLE IF NOT EXISTS `biz_material_base_price` (
    `id`             INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `material_id`    INT NOT NULL COMMENT '材料ID',
    `base_price`     DECIMAL(14,2) NOT NULL COMMENT '基准价格(含税)',
    `effective_date` DATE NOT NULL COMMENT '生效日期',
    `source`         VARCHAR(30) DEFAULT 'manual' COMMENT '来源: manual手动/contract合同自动',
    `source_id`      INT DEFAULT NULL COMMENT '来源ID(合同ID等)',
    `creator_id`     INT DEFAULT NULL COMMENT '创建人ID',
    `created_at`     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX `idx_material_id` (`material_id`),
    INDEX `idx_effective_date` (`effective_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='材料基准价格表';

-- 10. biz_material_price_history — 材料价格变动历史
CREATE TABLE IF NOT EXISTS `biz_material_price_history` (
    `id`            INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `material_id`   INT NOT NULL COMMENT '材料ID',
    `old_price`     DECIMAL(14,2) NOT NULL COMMENT '变动前价格',
    `new_price`     DECIMAL(14,2) NOT NULL COMMENT '变动后价格',
    `change_rate`   DECIMAL(5,2) DEFAULT NULL COMMENT '变动幅度(%)',
    `change_reason` VARCHAR(200) DEFAULT NULL COMMENT '变动原因',
    `source`        VARCHAR(30) DEFAULT NULL COMMENT '触发来源: contract/manual',
    `source_id`     INT DEFAULT NULL COMMENT '来源ID',
    `creator_id`    INT DEFAULT NULL COMMENT '操作人ID',
    `created_at`    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_material_id` (`material_id`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='材料价格变动历史表';

-- 11. biz_inventory_check_item — 盘点明细
CREATE TABLE IF NOT EXISTS `biz_inventory_check_item` (
    `id`              INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `check_id`        INT NOT NULL COMMENT '盘点单ID',
    `material_id`     INT NOT NULL COMMENT '材料ID',
    `material_name`   VARCHAR(100) DEFAULT NULL COMMENT '材料名称',
    `spec`            VARCHAR(100) DEFAULT NULL COMMENT '规格型号',
    `unit`            VARCHAR(20) DEFAULT NULL COMMENT '单位',
    `book_quantity`   DECIMAL(14,4) NOT NULL COMMENT '账面数量',
    `actual_quantity` DECIMAL(14,4) DEFAULT NULL COMMENT '实际数量',
    `diff_quantity`   DECIMAL(14,4) DEFAULT NULL COMMENT '差异数量(实际-账面)',
    `diff_reason`     VARCHAR(200) DEFAULT NULL COMMENT '差异原因',
    `created_at`      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_check_id` (`check_id`),
    INDEX `idx_material_id` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='盘点明细表';

-- 12. biz_inventory_record — 库存流水
CREATE TABLE IF NOT EXISTS `biz_inventory_record` (
    `id`              INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `project_id`      INT NOT NULL COMMENT '项目ID',
    `material_id`     INT NOT NULL COMMENT '材料ID',
    `biz_type`        VARCHAR(30) NOT NULL COMMENT '业务类型: inbound入库/outbound出库/return退库/check_gain盘盈/check_loss盘亏',
    `biz_id`          INT DEFAULT NULL COMMENT '业务单据ID',
    `biz_no`          VARCHAR(50) DEFAULT NULL COMMENT '业务单据编号',
    `direction`       TINYINT NOT NULL COMMENT '方向: 1入 -1出',
    `quantity`        DECIMAL(14,4) NOT NULL COMMENT '本次数量',
    `before_quantity` DECIMAL(14,4) NOT NULL COMMENT '变动前库存数量',
    `after_quantity`  DECIMAL(14,4) NOT NULL COMMENT '变动后库存数量',
    `unit_price`      DECIMAL(14,2) DEFAULT NULL COMMENT '单价',
    `amount`          DECIMAL(14,2) DEFAULT NULL COMMENT '金额',
    `created_at`      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_project_material` (`project_id`, `material_id`),
    INDEX `idx_biz_type_id` (`biz_type`, `biz_id`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='库存流水记录表';

-- 13. biz_gantt_progress_desc — 进度描述
CREATE TABLE IF NOT EXISTS `biz_gantt_progress_desc` (
    `id`             INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `task_id`        INT NOT NULL COMMENT '甘特图任务ID',
    `description`    TEXT NOT NULL COMMENT '进度描述内容',
    `attachment_ids` VARCHAR(500) DEFAULT NULL COMMENT '附件ID列表(逗号分隔)',
    `creator_id`     INT DEFAULT NULL COMMENT '填报人ID',
    `created_at`     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='甘特图进度描述表(追加型不可修改)';

-- 14. biz_income_split_item — 收入拆分明细
CREATE TABLE IF NOT EXISTS `biz_income_split_item` (
    `id`         INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `split_id`   INT NOT NULL COMMENT '收入拆分ID(关联biz_income_split)',
    `task_name`  VARCHAR(200) NOT NULL COMMENT '任务名称',
    `amount`     DECIMAL(14,2) NOT NULL COMMENT '拆分金额',
    `ratio`      DECIMAL(5,2) DEFAULT NULL COMMENT '占比(%)',
    `remark`     VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_split_id` (`split_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收入拆分明细表';

-- 15. biz_payment_inbound_rel — 付款入库关联
CREATE TABLE IF NOT EXISTS `biz_payment_inbound_rel` (
    `payment_id` INT NOT NULL COMMENT '付款申请ID',
    `inbound_id` INT NOT NULL COMMENT '入库单ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`payment_id`, `inbound_id`),
    INDEX `idx_inbound_id` (`inbound_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='付款-入库关联表';

-- 16. biz_report_cache — 报表缓存
CREATE TABLE IF NOT EXISTS `biz_report_cache` (
    `id`           INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `report_type`  VARCHAR(30) NOT NULL COMMENT '报表类型: project_cost/income_expense/purchase_progress/inventory_summary',
    `project_id`   INT DEFAULT NULL COMMENT '项目ID(null表示全局汇总)',
    `data_json`    JSON NOT NULL COMMENT '预聚合数据(JSON)',
    `generated_at` DATETIME NOT NULL COMMENT '生成时间',
    `expired_at`   DATETIME NOT NULL COMMENT '过期时间',
    `created_at`   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_report_type` (`report_type`),
    INDEX `idx_project_id` (`project_id`),
    INDEX `idx_expired_at` (`expired_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报表预聚合缓存表';

-- 17. biz_report_subscribe — 报表订阅
CREATE TABLE IF NOT EXISTS `biz_report_subscribe` (
    `id`          INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `user_id`     INT NOT NULL COMMENT '订阅用户ID',
    `report_type` VARCHAR(30) NOT NULL COMMENT '报表类型',
    `project_id`  INT DEFAULT NULL COMMENT '项目ID(null表示全部)',
    `frequency`   VARCHAR(20) NOT NULL COMMENT '推送频率: daily/weekly/monthly',
    `status`      TINYINT DEFAULT 1 COMMENT '状态: 0取消 1订阅中',
    `created_at`  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_user_report` (`user_id`, `report_type`, `project_id`),
    INDEX `idx_frequency_status` (`frequency`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报表订阅配置表';

-- P1 附加 DDL：sys_todo 新增 read_at 字段（消息已读回执）
ALTER TABLE `sys_todo` ADD COLUMN IF NOT EXISTS `read_at` DATETIME DEFAULT NULL
    COMMENT '已读时间' AFTER `status`;
