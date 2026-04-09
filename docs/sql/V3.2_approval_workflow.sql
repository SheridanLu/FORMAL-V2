-- ============================================================
-- MOCHU-OA V3.2 — 审批工作流增量迁移
-- 执行方式: docker exec -i mochu-mysql mysql -uroot -p'mochu@2026' mochu_oa < docs/sql/V3.2_approval_workflow.sql
-- ============================================================

USE mochu_oa;

-- ============================================================
-- 1. 扩展 biz_approval_instance 表
-- ============================================================
ALTER TABLE biz_approval_instance
  ADD COLUMN deadline_at     DATETIME   DEFAULT NULL COMMENT '当前节点进入时间(用于超时计算)' AFTER initiator_id,
  ADD COLUMN reminder_level  TINYINT    NOT NULL DEFAULT 0 COMMENT '0未提醒/1已24h/2已48h/3已72h' AFTER deadline_at;

-- ============================================================
-- 2. 会签(加签)表
-- ============================================================
CREATE TABLE IF NOT EXISTS biz_approval_cosign (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  instance_id   INT           NOT NULL COMMENT '关联审批实例ID',
  node_order    INT           NOT NULL COMMENT '节点序号',
  cosigner_id   INT           NOT NULL COMMENT '会签人用户ID',
  status        VARCHAR(20)   NOT NULL DEFAULT 'pending' COMMENT 'pending/approved/rejected',
  opinion       VARCHAR(500)  DEFAULT NULL COMMENT '会签意见',
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  completed_at  DATETIME      DEFAULT NULL,
  KEY idx_cosign_instance (instance_id, node_order),
  KEY idx_cosign_user (cosigner_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='审批会签(加签)表';

-- ============================================================
-- 3. 抄送/阅办/阅知表
-- ============================================================
CREATE TABLE IF NOT EXISTS biz_approval_cc (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  instance_id   INT           NOT NULL COMMENT '关联审批实例ID',
  user_id       INT           NOT NULL COMMENT '接收人用户ID',
  cc_type       VARCHAR(20)   NOT NULL COMMENT 'read_handle阅办/read_ack阅知',
  is_read       TINYINT       NOT NULL DEFAULT 0 COMMENT '0未读/1已读',
  is_handled    TINYINT       NOT NULL DEFAULT 0 COMMENT '0未处理/1已处理(仅阅办)',
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  handled_at    DATETIME      DEFAULT NULL,
  KEY idx_cc_instance (instance_id),
  KEY idx_cc_user (user_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='审批抄送/阅办/阅知表';

-- ============================================================
-- 4. 种子数据：16条审批流程定义
-- ============================================================

-- 4.1 项目立项(虚拟转实体): 采购员→财务→总经理
INSERT INTO sys_flow_def (biz_type, flow_name, nodes_json, condition_json, status, version, creator_id, deleted) VALUES
('project', '项目立项审批',
 '[{"node_order":1,"node_name":"采购员审核","approver_type":"role","approver_id":4},{"node_order":2,"node_name":"财务审核","approver_type":"role","approver_id":5},{"node_order":3,"node_name":"总经理审批","approver_type":"role","approver_id":1}]',
 NULL, 1, 1, 1, 0);

-- 4.2 合同(常规): 采购员→财务→法务→总经理
INSERT INTO sys_flow_def (biz_type, flow_name, nodes_json, condition_json, status, version, creator_id, deleted) VALUES
('contract', '常规合同审批',
 '[{"node_order":1,"node_name":"采购员审核","approver_type":"role","approver_id":4},{"node_order":2,"node_name":"财务审核","approver_type":"role","approver_id":5},{"node_order":3,"node_name":"法务审核","approver_type":"role","approver_id":6},{"node_order":4,"node_name":"总经理审批","approver_type":"role","approver_id":1}]',
 NULL, 1, 1, 1, 0);

-- 4.3 合同(支出超量): 采购员→预算员→财务→法务→总经理
INSERT INTO sys_flow_def (biz_type, flow_name, nodes_json, condition_json, status, version, creator_id, deleted) VALUES
('contract', '支出合同超量审批',
 '[{"node_order":1,"node_name":"采购员审核","approver_type":"role","approver_id":4},{"node_order":2,"node_name":"预算员审核","approver_type":"role","approver_id":3},{"node_order":3,"node_name":"财务审核","approver_type":"role","approver_id":5},{"node_order":4,"node_name":"法务审核","approver_type":"role","approver_id":6},{"node_order":5,"node_name":"总经理审批","approver_type":"role","approver_id":1}]',
 '{"field":"contract_type","op":"eq","value":"expense","and":{"field":"over_budget","op":"eq","value":true}}',
 1, 2, 1, 0);

-- 4.4 采购清单: 预算员→财务→总经理
INSERT INTO sys_flow_def (biz_type, flow_name, nodes_json, condition_json, status, version, creator_id, deleted) VALUES
('purchase', '采购清单审批',
 '[{"node_order":1,"node_name":"预算员审核","approver_type":"role","approver_id":3},{"node_order":2,"node_name":"财务审核","approver_type":"role","approver_id":5},{"node_order":3,"node_name":"总经理审批","approver_type":"role","approver_id":1}]',
 NULL, 1, 1, 1, 0);

-- 4.5 零星采购(常规): 采购员→总经理
INSERT INTO sys_flow_def (biz_type, flow_name, nodes_json, condition_json, status, version, creator_id, deleted) VALUES
('spot_purchase', '零星采购审批',
 '[{"node_order":1,"node_name":"采购员审核","approver_type":"role","approver_id":4},{"node_order":2,"node_name":"总经理审批","approver_type":"role","approver_id":1}]',
 NULL, 1, 1, 1, 0);

-- 4.6 零星采购(超阈值): 采购员→预算员→总经理
INSERT INTO sys_flow_def (biz_type, flow_name, nodes_json, condition_json, status, version, creator_id, deleted) VALUES
('spot_purchase', '零星采购超阈值审批',
 '[{"node_order":1,"node_name":"采购员审核","approver_type":"role","approver_id":4},{"node_order":2,"node_name":"预算员审核","approver_type":"role","approver_id":3},{"node_order":3,"node_name":"总经理审批","approver_type":"role","approver_id":1}]',
 '{"field":"amount","op":"gt","value":5000}',
 1, 2, 1, 0);

-- 4.7 入库单: 采购员→财务
INSERT INTO sys_flow_def (biz_type, flow_name, nodes_json, condition_json, status, version, creator_id, deleted) VALUES
('inbound', '入库单审批',
 '[{"node_order":1,"node_name":"采购员审核","approver_type":"role","approver_id":4},{"node_order":2,"node_name":"财务审核","approver_type":"role","approver_id":5}]',
 NULL, 1, 1, 1, 0);

-- 4.8 出库单: 项目经理→采购员确认→财务→总经理
INSERT INTO sys_flow_def (biz_type, flow_name, nodes_json, condition_json, status, version, creator_id, deleted) VALUES
('outbound', '出库单审批',
 '[{"node_order":1,"node_name":"项目经理审核","approver_type":"role","approver_id":2},{"node_order":2,"node_name":"采购员确认","approver_type":"role","approver_id":4},{"node_order":3,"node_name":"财务审核","approver_type":"role","approver_id":5},{"node_order":4,"node_name":"总经理审批","approver_type":"role","approver_id":1}]',
 NULL, 1, 1, 1, 0);

-- 4.9 退库单: 项目经理→采购员确认→财务→总经理
INSERT INTO sys_flow_def (biz_type, flow_name, nodes_json, condition_json, status, version, creator_id, deleted) VALUES
('return_order', '退库单审批',
 '[{"node_order":1,"node_name":"项目经理审核","approver_type":"role","approver_id":2},{"node_order":2,"node_name":"采购员确认","approver_type":"role","approver_id":4},{"node_order":3,"node_name":"财务审核","approver_type":"role","approver_id":5},{"node_order":4,"node_name":"总经理审批","approver_type":"role","approver_id":1}]',
 NULL, 1, 1, 1, 0);

-- 4.10 盘点单: 采购员→财务→总经理
INSERT INTO sys_flow_def (biz_type, flow_name, nodes_json, condition_json, status, version, creator_id, deleted) VALUES
('inventory_check', '盘点单审批',
 '[{"node_order":1,"node_name":"采购员审核","approver_type":"role","approver_id":4},{"node_order":2,"node_name":"财务审核","approver_type":"role","approver_id":5},{"node_order":3,"node_name":"总经理审批","approver_type":"role","approver_id":1}]',
 NULL, 1, 1, 1, 0);

-- 4.11 甘特图/里程碑: 项目经理→总经理
INSERT INTO sys_flow_def (biz_type, flow_name, nodes_json, condition_json, status, version, creator_id, deleted) VALUES
('gantt_task', '里程碑进度审批',
 '[{"node_order":1,"node_name":"项目经理审核","approver_type":"role","approver_id":2},{"node_order":2,"node_name":"总经理审批","approver_type":"role","approver_id":1}]',
 NULL, 1, 1, 1, 0);

-- 4.12 变更单(现场签证/甲方变更/劳务签证): 项目经理→预算员→总经理
INSERT INTO sys_flow_def (biz_type, flow_name, nodes_json, condition_json, status, version, creator_id, deleted) VALUES
('change_order', '变更单审批',
 '[{"node_order":1,"node_name":"项目经理审核","approver_type":"role","approver_id":2},{"node_order":2,"node_name":"预算员审核","approver_type":"role","approver_id":3},{"node_order":3,"node_name":"总经理审批","approver_type":"role","approver_id":1}]',
 NULL, 1, 1, 1, 0);

-- 4.13 对账单: 项目经理→采购员→预算员→财务→总经理
INSERT INTO sys_flow_def (biz_type, flow_name, nodes_json, condition_json, status, version, creator_id, deleted) VALUES
('statement', '对账单审批',
 '[{"node_order":1,"node_name":"项目经理确认","approver_type":"role","approver_id":2},{"node_order":2,"node_name":"采购员审核","approver_type":"role","approver_id":4},{"node_order":3,"node_name":"预算员审核","approver_type":"role","approver_id":3},{"node_order":4,"node_name":"财务审核","approver_type":"role","approver_id":5},{"node_order":5,"node_name":"总经理审批","approver_type":"role","approver_id":1}]',
 NULL, 1, 1, 1, 0);

-- 4.14 付款申请: 项目经理→采购员→财务→总经理
INSERT INTO sys_flow_def (biz_type, flow_name, nodes_json, condition_json, status, version, creator_id, deleted) VALUES
('payment', '付款申请审批',
 '[{"node_order":1,"node_name":"项目经理审核","approver_type":"role","approver_id":2},{"node_order":2,"node_name":"采购员审核","approver_type":"role","approver_id":4},{"node_order":3,"node_name":"财务审核","approver_type":"role","approver_id":5},{"node_order":4,"node_name":"总经理审批","approver_type":"role","approver_id":1}]',
 NULL, 1, 1, 1, 0);

-- 4.15 报销: 员工提交→主管审批→财务审批→财务付款确认
INSERT INTO sys_flow_def (biz_type, flow_name, nodes_json, condition_json, status, version, creator_id, deleted) VALUES
('reimburse', '报销审批',
 '[{"node_order":1,"node_name":"员工提交","approver_type":"role","approver_id":9},{"node_order":2,"node_name":"主管审批","approver_type":"dept_leader","approver_id":null},{"node_order":3,"node_name":"财务审批","approver_type":"role","approver_id":5},{"node_order":4,"node_name":"财务付款确认","approver_type":"role","approver_id":5}]',
 NULL, 1, 1, 1, 0);

-- 4.16 完工验收: 项目经理→预算员→采购员→总经理
INSERT INTO sys_flow_def (biz_type, flow_name, nodes_json, condition_json, status, version, creator_id, deleted) VALUES
('completion', '完工验收审批',
 '[{"node_order":1,"node_name":"项目经理审核","approver_type":"role","approver_id":2},{"node_order":2,"node_name":"预算员审核","approver_type":"role","approver_id":3},{"node_order":3,"node_name":"采购员审核","approver_type":"role","approver_id":4},{"node_order":4,"node_name":"总经理审批","approver_type":"role","approver_id":1}]',
 NULL, 1, 1, 1, 0);

-- 4.17 劳务结算: 项目经理→预算员→采购员→财务→总经理
INSERT INTO sys_flow_def (biz_type, flow_name, nodes_json, condition_json, status, version, creator_id, deleted) VALUES
('labor_settlement', '劳务结算审批',
 '[{"node_order":1,"node_name":"项目经理审核","approver_type":"role","approver_id":2},{"node_order":2,"node_name":"预算员审核","approver_type":"role","approver_id":3},{"node_order":3,"node_name":"采购员审核","approver_type":"role","approver_id":4},{"node_order":4,"node_name":"财务审核","approver_type":"role","approver_id":5},{"node_order":5,"node_name":"总经理审批","approver_type":"role","approver_id":1}]',
 NULL, 1, 1, 1, 0);

-- 4.18 工资表: HR→财务→总经理
INSERT INTO sys_flow_def (biz_type, flow_name, nodes_json, condition_json, status, version, creator_id, deleted) VALUES
('salary', '工资表审批',
 '[{"node_order":1,"node_name":"HR审核","approver_type":"role","approver_id":8},{"node_order":2,"node_name":"财务审核","approver_type":"role","approver_id":5},{"node_order":3,"node_name":"总经理审批","approver_type":"role","approver_id":1}]',
 NULL, 1, 1, 1, 0);
