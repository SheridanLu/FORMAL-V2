-- V3.2 细粒度权限种子数据
-- 清理旧权限
DELETE FROM sys_permission WHERE 1=1;

-- 系统管理 (10)
INSERT INTO sys_permission (perm_code, perm_name, module, perm_type) VALUES
('system:user-manage', '用户管理', 'system', 1),
('system:role-manage', '角色管理', 'system', 1),
('system:dept-manage', '部门管理', 'system', 1),
('system:announcement-manage', '公告管理', 'system', 1),
('system:audit-log', '审计日志查看', 'system', 1),
('system:config', '系统配置查看', 'system', 1),
('system:config:edit', '系统配置编辑', 'system', 1),
('system:delegation', '委托代理查看', 'system', 1),
('system:delegation:edit', '委托代理编辑', 'system', 1),
('system:tpl-manage', '合同模板管理', 'system', 1);

-- 项目管理 (6)
INSERT INTO sys_permission (perm_code, perm_name, module, perm_type) VALUES
('project:create', '项目创建', 'project', 1),
('project:view-all', '查看所有项目', 'project', 1),
('project:view-own', '查看本人项目', 'project', 1),
('project:edit', '项目编辑', 'project', 1),
('project:delete', '项目删除', 'project', 1),
('project:suspend', '项目暂停/恢复', 'project', 1);

-- 合同管理 (6)
INSERT INTO sys_permission (perm_code, perm_name, module, perm_type) VALUES
('contract:create', '合同创建', 'contract', 1),
('contract:view-all', '查看所有合同', 'contract', 1),
('contract:view-own', '查看本人合同', 'contract', 1),
('contract:edit', '合同编辑', 'contract', 1),
('contract:delete', '合同删除', 'contract', 1),
('contract:terminate', '合同终止', 'contract', 1);

-- 采购管理 (4)
INSERT INTO sys_permission (perm_code, perm_name, module, perm_type) VALUES
('purchase:create', '采购单创建', 'purchase', 1),
('purchase:view', '采购单查看', 'purchase', 1),
('purchase:edit', '采购单编辑', 'purchase', 1),
('purchase:delete', '采购单删除', 'purchase', 1);

-- 物资管理 (4)
INSERT INTO sys_permission (perm_code, perm_name, module, perm_type) VALUES
('material:view', '物资查看', 'material', 1),
('material:edit', '物资编辑', 'material', 1),
('material:inbound', '入库操作', 'material', 1),
('material:outbound', '出库操作', 'material', 1);

-- 库存管理 (6)
INSERT INTO sys_permission (perm_code, perm_name, module, perm_type) VALUES
('inventory:inbound', '入库单管理', 'inventory', 1),
('inventory:outbound', '出库单管理', 'inventory', 1),
('inventory:return', '退库单管理', 'inventory', 1),
('inventory:stock-view', '库存查看', 'inventory', 1),
('inventory:check', '盘点管理', 'inventory', 1),
('inventory:check-approve', '盘点审批', 'inventory', 1);

-- 财务管理 (8)
INSERT INTO sys_permission (perm_code, perm_name, module, perm_type) VALUES
('finance:statement-manage', '对账单管理', 'finance', 1),
('finance:payment-create', '付款申请创建', 'finance', 1),
('finance:payment-confirm', '付款确认', 'finance', 1),
('finance:invoice-manage', '发票管理', 'finance', 1),
('finance:reimburse-manage', '报销管理', 'finance', 1),
('finance:receipt-create', '收款登记', 'finance', 1),
('finance:cost-view', '成本台账查看', 'finance', 1),
('finance:cost-summary', '成本汇总查看', 'finance', 1);

-- 人力资源 (8)
INSERT INTO sys_permission (perm_code, perm_name, module, perm_type) VALUES
('hr:salary-manage', '薪资管理', 'hr', 1),
('hr:salary-config', '薪资配置', 'hr', 1),
('hr:contract-manage', '劳动合同管理', 'hr', 1),
('hr:certificate-manage', '证书管理', 'hr', 1),
('hr:entry-manage', '入职管理', 'hr', 1),
('hr:resign-manage', '离职管理', 'hr', 1),
('hr:social-insurance', '社保配置', 'hr', 1),
('hr:asset-transfer', '资产交接', 'hr', 1);

-- 进度管理 (4)
INSERT INTO sys_permission (perm_code, perm_name, module, perm_type) VALUES
('progress:gantt-manage', '甘特图管理', 'progress', 1),
('progress:milestone-manage', '里程碑管理', 'progress', 1),
('progress:change-manage', '变更单管理', 'progress', 1),
('progress:report', '进度汇报', 'progress', 1);

-- 竣工管理 (4)
INSERT INTO sys_permission (perm_code, perm_name, module, perm_type) VALUES
('completion:finish-manage', '完工验收管理', 'completion', 1),
('completion:labor-manage', '劳务结算管理', 'completion', 1),
('completion:drawing-manage', '竣工图纸管理', 'completion', 1),
('completion:doc-manage', '竣工资料管理', 'completion', 1);

-- 审批管理 (3)
INSERT INTO sys_permission (perm_code, perm_name, module, perm_type) VALUES
('approval:flow-manage', '审批流程管理', 'approval', 1),
('approval:operate', '审批操作', 'approval', 1),
('approval:view', '审批查看', 'approval', 1);

-- 报表 (2)
INSERT INTO sys_permission (perm_code, perm_name, module, perm_type) VALUES
('report:view', '报表查看', 'report', 1),
('report:export', '报表导出', 'report', 1);

-- 供应商 (2)
INSERT INTO sys_permission (perm_code, perm_name, module, perm_type) VALUES
('supplier:view', '供应商查看', 'supplier', 1),
('supplier:edit', '供应商编辑', 'supplier', 1);
