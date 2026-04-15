-- V3.2.0.26: 广联达导入记录表
CREATE TABLE IF NOT EXISTS `biz_glodon_import` (
    `id`            INT          NOT NULL AUTO_INCREMENT,
    `project_id`    INT          NOT NULL             COMMENT '关联项目ID',
    `file_name`     VARCHAR(200) NOT NULL             COMMENT '导入文件名',
    `file_url`      VARCHAR(500) NULL                 COMMENT '文件地址',
    `import_type`   VARCHAR(50)  NOT NULL DEFAULT 'cost' COMMENT '导入类型:cost/quantity/price',
    `status`        VARCHAR(20)  NOT NULL DEFAULT 'pending' COMMENT 'pending/processing/success/failed',
    `total_rows`    INT          NULL DEFAULT 0       COMMENT '总行数',
    `success_rows`  INT          NULL DEFAULT 0       COMMENT '成功行数',
    `error_msg`     TEXT         NULL                 COMMENT '错误信息',
    `creator_id`    INT          NULL,
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`       TINYINT(1)   NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_glodon_project` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广联达数据导入记录';
