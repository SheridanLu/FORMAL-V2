-- V3.2.0.01__add_role_data_scope.sql
-- 角色数据权限范围表
CREATE TABLE IF NOT EXISTS `sys_role_data_scope` (
    `id`         INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `role_id`    INT NOT NULL COMMENT '角色ID',
    `data_scope` TINYINT NOT NULL DEFAULT 1 COMMENT '数据范围',
    `dept_id`    INT DEFAULT NULL COMMENT '自定义部门ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
  COMMENT='角色数据权限范围表';
