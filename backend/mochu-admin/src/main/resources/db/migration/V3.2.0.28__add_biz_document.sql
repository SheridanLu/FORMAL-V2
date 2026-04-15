-- V3.2: 文档管理模块
CREATE TABLE IF NOT EXISTS `biz_document` (
    `id`            INT AUTO_INCREMENT PRIMARY KEY,
    `file_name`     VARCHAR(500) NOT NULL COMMENT '文件名',
    `file_url`      VARCHAR(1000) NULL COMMENT '文件访问URL',
    `file_path`     VARCHAR(1000) NULL COMMENT '文件存储路径',
    `file_size`     BIGINT       DEFAULT 0 COMMENT '文件大小(字节)',
    `file_type`     VARCHAR(100) NULL COMMENT 'MIME类型',
    `category`      VARCHAR(50)  DEFAULT 'other' COMMENT '分类:construction/contract/completion/design/other',
    `project_id`    INT          NULL COMMENT '关联项目ID',
    `uploader_id`   INT          NULL COMMENT '上传人ID',
    `version`       INT          DEFAULT 1 COMMENT '版本号',
    `remark`        VARCHAR(500) NULL COMMENT '备注',
    `creator_id`    INT          NULL COMMENT '创建人ID',
    `created_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`       TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    INDEX `idx_document_project` (`project_id`),
    INDEX `idx_document_category` (`category`),
    INDEX `idx_document_uploader` (`uploader_id`),
    INDEX `idx_document_created` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文档管理';
