-- V3.2.0.12__add_report_tables.sql
-- 报表快照表
CREATE TABLE IF NOT EXISTS `sys_report_snapshot` (
    `id`            INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `template_id`   INT NOT NULL COMMENT '报表模板ID',
    `report_name`   VARCHAR(200) NOT NULL COMMENT '报表名称',
    `params_json`   TEXT COMMENT '生成时的参数JSON',
    `result_json`   LONGTEXT COMMENT '报表结果JSON',
    `row_count`     INT DEFAULT 0 COMMENT '数据行数',
    `generated_at`  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '生成时间',
    `creator_id`    INT,
    `created_at`    DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_template_id` (`template_id`),
    INDEX `idx_generated_at` (`generated_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
  COMMENT='报表快照表';

-- 报表收藏表
CREATE TABLE IF NOT EXISTS `sys_report_favorite` (
    `id`            INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `user_id`       INT NOT NULL COMMENT '用户ID',
    `template_id`   INT NOT NULL COMMENT '报表模板ID',
    `created_at`    DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_user_template` (`user_id`, `template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
  COMMENT='报表收藏表';
