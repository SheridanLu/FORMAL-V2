-- ====================================================================
-- V3.5 里程碑依赖关系管理
-- 日期: 2026-04-08
-- 说明: 新增里程碑依赖关系表，支持多对多依赖关联
-- ====================================================================

-- 1. 里程碑依赖关系表（多对多）
CREATE TABLE IF NOT EXISTS biz_milestone_dep (
  id         INT PRIMARY KEY AUTO_INCREMENT,
  milestone_id    INT NOT NULL COMMENT '当前里程碑ID (biz_gantt_task.id)',
  dep_milestone_id INT NOT NULL COMMENT '前置依赖里程碑ID (biz_gantt_task.id)',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_milestone_dep (milestone_id, dep_milestone_id),
  KEY idx_dep_milestone (dep_milestone_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='里程碑依赖关系';

-- 2. 验证
SELECT '=== V3.5 里程碑依赖关系表创建完成 ===' AS info;
