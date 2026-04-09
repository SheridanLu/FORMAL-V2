-- ============================================================
-- MOCHU-OA V3.2 — 权限数据初始化
-- 补充 sys_permission + sys_role_permission
-- 执行方式: docker exec -i mochu-mysql mysql -uroot -p'mochu@2026' mochu_oa < docs/sql/V3.2_permissions_seed.sql
-- ============================================================

USE mochu_oa;

-- ============================================================
-- 1. 插入所有权限定义
-- ============================================================
INSERT INTO sys_permission (perm_code, perm_name, module, perm_type) VALUES
-- 系统管理模块
('system:user-manage',          '用户管理',       'system',     1),
('system:role-manage',          '角色管理',       'system',     1),
('system:dept-manage',          '部门管理',       'system',     1),
('system:announcement-manage',  '公告管理',       'system',     1),
('system:audit-log',            '审计日志查看',   'system',     1),
('system:config',               '系统配置查看',   'system',     1),
('system:config:edit',          '系统配置编辑',   'system',     1),
('system:delegation',           '委托代理查看',   'system',     1),
('system:delegation:edit',      '委托代理编辑',   'system',     1),
-- 项目管理模块
('project:view',                '项目查看',       'project',    1),
('project:edit',                '项目编辑',       'project',    1),
-- 供应商管理模块
('supplier:view',               '供应商查看',     'supplier',   1),
('supplier:edit',               '供应商编辑',     'supplier',   1),
-- 材料管理模块
('material:view',               '材料查看',       'material',   1),
('material:edit',               '材料编辑',       'material',   1),
-- 合同管理模块
('contract:view',               '合同查看',       'contract',   1),
('contract:edit',               '合同编辑',       'contract',   1),
-- 采购管理模块
('purchase:view',               '采购查看',       'purchase',   1),
('purchase:edit',               '采购编辑',       'purchase',   1),
-- 库存管理模块
('inventory:view',              '库存查看',       'inventory',  1),
('inventory:edit',              '库存编辑',       'inventory',  1),
-- 进度变更模块
('progress:view',               '进度查看',       'progress',   1),
('progress:edit',               '进度编辑',       'progress',   1),
-- 财务管理模块
('finance:view',                '财务查看',       'finance',    1),
('finance:edit',                '财务编辑',       'finance',    1),
-- 人力资源模块
('hr:view',                     '人力资源查看',   'hr',         1),
('hr:edit',                     '人力资源编辑',   'hr',         1),
-- 竣工劳务模块
('completion:view',             '竣工劳务查看',   'completion', 1),
('completion:edit',             '竣工劳务编辑',   'completion', 1)
ON DUPLICATE KEY UPDATE perm_name = VALUES(perm_name);

-- ============================================================
-- 2. 为 SOFT/软件管理员 (role_id=10) 分配全部权限
--    admin 用户绑定的就是这个角色
-- ============================================================
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 10, id FROM sys_permission
ON DUPLICATE KEY UPDATE role_id = role_id;

-- ============================================================
-- 3. 为 GM/总经理 (role_id=1) 也分配全部权限
-- ============================================================
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission
ON DUPLICATE KEY UPDATE role_id = role_id;

-- ============================================================
-- 4. 按角色职能分配业务权限
-- ============================================================

-- PROJ_MGR/项目经理 (role_id=2): 项目、合同、进度、竣工的查看和编辑
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 2, id FROM sys_permission WHERE perm_code IN (
  'project:view','project:edit',
  'contract:view','contract:edit',
  'supplier:view',
  'material:view',
  'purchase:view',
  'inventory:view',
  'progress:view','progress:edit',
  'finance:view',
  'hr:view',
  'completion:view','completion:edit'
) ON DUPLICATE KEY UPDATE role_id = role_id;

-- BUDGET/预算员 (role_id=3): 项目、合同、材料查看，财务查看编辑
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 3, id FROM sys_permission WHERE perm_code IN (
  'project:view',
  'contract:view','contract:edit',
  'material:view','material:edit',
  'finance:view','finance:edit',
  'progress:view'
) ON DUPLICATE KEY UPDATE role_id = role_id;

-- PURCHASE/采购员 (role_id=4): 采购、供应商、材料、库存、合同
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 4, id FROM sys_permission WHERE perm_code IN (
  'project:view',
  'supplier:view','supplier:edit',
  'material:view','material:edit',
  'contract:view',
  'purchase:view','purchase:edit',
  'inventory:view','inventory:edit'
) ON DUPLICATE KEY UPDATE role_id = role_id;

-- FINANCE/财务人员 (role_id=5): 财务全部，合同、采购、库存查看
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 5, id FROM sys_permission WHERE perm_code IN (
  'project:view',
  'contract:view',
  'purchase:view',
  'inventory:view',
  'finance:view','finance:edit',
  'hr:view'
) ON DUPLICATE KEY UPDATE role_id = role_id;

-- LEGAL/法务人员 (role_id=6): 合同查看编辑，竣工案件查看
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 6, id FROM sys_permission WHERE perm_code IN (
  'project:view',
  'contract:view','contract:edit',
  'completion:view'
) ON DUPLICATE KEY UPDATE role_id = role_id;

-- DATA/资料员 (role_id=7): 大部分模块查看
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 7, id FROM sys_permission WHERE perm_code IN (
  'project:view',
  'contract:view',
  'supplier:view',
  'material:view',
  'purchase:view',
  'inventory:view',
  'progress:view',
  'completion:view'
) ON DUPLICATE KEY UPDATE role_id = role_id;

-- HR/HR管理员 (role_id=8): 人力资源全部
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 8, id FROM sys_permission WHERE perm_code IN (
  'hr:view','hr:edit',
  'project:view'
) ON DUPLICATE KEY UPDATE role_id = role_id;

-- BASE/基层员工 (role_id=9): 项目、进度查看
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 9, id FROM sys_permission WHERE perm_code IN (
  'project:view',
  'progress:view',
  'inventory:view'
) ON DUPLICATE KEY UPDATE role_id = role_id;

-- TEAM_MEMBER/班组成员 (role_id=11): 进度、竣工查看
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 11, id FROM sys_permission WHERE perm_code IN (
  'project:view',
  'progress:view',
  'completion:view'
) ON DUPLICATE KEY UPDATE role_id = role_id;
