-- V3.0: 首页轮播图/Banner管理
CREATE TABLE IF NOT EXISTS `sys_banner` (
    `id`          INT AUTO_INCREMENT PRIMARY KEY,
    `title`       VARCHAR(200)  NOT NULL COMMENT '标题',
    `image_url`   VARCHAR(500)  NOT NULL COMMENT '图片URL',
    `link_url`    VARCHAR(500)  NULL COMMENT '点击跳转链接',
    `sort_order`  INT           DEFAULT 0 COMMENT '排序(越大越靠前)',
    `status`      TINYINT       DEFAULT 1 COMMENT '状态:1启用 0禁用',
    `start_time`  DATETIME      NULL COMMENT '展示开始时间',
    `end_time`    DATETIME      NULL COMMENT '展示结束时间',
    `remark`      VARCHAR(500)  NULL COMMENT '备注',
    `creator_id`  INT           NULL COMMENT '创建人ID',
    `created_at`  DATETIME      DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT       DEFAULT 0 COMMENT '逻辑删除',
    INDEX `idx_banner_sort` (`status`, `sort_order` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='首页轮播图';
