-- V3.2.0.14__add_user_shortcut_config.sql
-- 用户快捷入口配置表
CREATE TABLE IF NOT EXISTS `sys_user_shortcut` (
    `id`            INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `user_id`       INT NOT NULL COMMENT '用户ID',
    `menu_code`     VARCHAR(100) NOT NULL COMMENT '菜单编码',
    `menu_name`     VARCHAR(100) DEFAULT '' COMMENT '菜单名称',
    `menu_icon`     VARCHAR(50) DEFAULT '' COMMENT '菜单图标',
    `menu_path`     VARCHAR(255) DEFAULT '' COMMENT '菜单路径',
    `sort_order`    INT DEFAULT 0 COMMENT '排序',
    `created_at`    DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
  COMMENT='用户快捷入口配置表';
