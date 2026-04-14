-- 角色数据权限范围表
CREATE TABLE IF NOT EXISTS `sys_role_data_scope` (
    `id`         INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `role_id`    INT NOT NULL COMMENT '角色ID',
    `data_scope` TINYINT NOT NULL DEFAULT 1 COMMENT '数据范围: 1=全部 2=本部门 3=本项目 4=仅本人 5=自定义',
    `dept_id`    INT DEFAULT NULL COMMENT '自定义部门ID(data_scope=5时使用)',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_role_id` (`role_id`),
    INDEX `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色数据权限范围表';
