-- ============================================================
-- MOCHU-OA 施工管理系统 V3.2 — 完整数据库DDL
-- 数据库: mochu_oa
-- 字符集: utf8mb4 / utf8mb4_general_ci
-- 生成日期: 2026-04-06
-- ============================================================

CREATE DATABASE IF NOT EXISTS mochu_oa
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE mochu_oa;

-- ============================================================
-- 第一部分：系统管理表 (sys_)
-- ============================================================

-- P.1 用户表
CREATE TABLE sys_user (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  username      VARCHAR(50)   NOT NULL COMMENT '登录名',
  real_name     VARCHAR(50)   NOT NULL COMMENT '姓名',
  phone         VARCHAR(20)   NOT NULL COMMENT '手机号',
  email         VARCHAR(100)  DEFAULT NULL COMMENT '邮箱',
  dept_id       INT           NOT NULL COMMENT '部门ID',
  position      VARCHAR(100)  DEFAULT NULL COMMENT '职位',
  password_hash VARCHAR(255)  NOT NULL COMMENT 'BCrypt加盐哈希密码',
  avatar        VARCHAR(500)  DEFAULT NULL COMMENT '头像URL',
  status        TINYINT       NOT NULL DEFAULT 1 COMMENT '1启用/0禁用',
  flag_contact  TINYINT       NOT NULL DEFAULT 1 COMMENT '是否在通讯录显示',
  privacy_mode  TINYINT       NOT NULL DEFAULT 0 COMMENT '隐私模式',
  login_attempts INT          NOT NULL DEFAULT 0 COMMENT '登录失败次数',
  last_login_time DATETIME    DEFAULT NULL COMMENT '最后登录时间',
  lock_until    DATETIME      DEFAULT NULL COMMENT '锁定到期时间',
  force_change_pwd TINYINT    NOT NULL DEFAULT 0 COMMENT '首次登录是否强制改密',
  wx_userid     VARCHAR(100)  DEFAULT NULL COMMENT '企业微信用户ID',
  creator_id    INT           NOT NULL COMMENT '创建人ID',
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted       TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  UNIQUE KEY uk_user_username (username),
  UNIQUE KEY uk_user_phone (phone),
  KEY idx_user_dept (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- P.2 部门表
CREATE TABLE sys_dept (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  name          VARCHAR(100)  NOT NULL COMMENT '部门名称',
  parent_id     INT           NOT NULL DEFAULT 0 COMMENT '上级部门ID,0为根',
  level         INT           NOT NULL COMMENT '层级,根部门=1',
  path          VARCHAR(500)  NOT NULL COMMENT '路径如"/1/2/21/"',
  sort          INT           NOT NULL DEFAULT 0 COMMENT '排序号',
  leader_id     INT           DEFAULT NULL COMMENT '负责人ID',
  phone         VARCHAR(20)   DEFAULT NULL COMMENT '部门电话',
  remark        VARCHAR(255)  DEFAULT NULL COMMENT '备注',
  status        TINYINT       NOT NULL DEFAULT 1 COMMENT '1启用/0停用',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  KEY idx_dept_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='部门表';

-- P.3 角色表
CREATE TABLE sys_role (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  role_code     VARCHAR(50)   NOT NULL COMMENT '角色编码',
  role_name     VARCHAR(100)  NOT NULL COMMENT '角色名称',
  data_scope    TINYINT       NOT NULL DEFAULT 4 COMMENT '数据权限:1全部/2本部门/3本项目/4仅个人/5指定范围',
  remark        VARCHAR(255)  DEFAULT NULL,
  status        TINYINT       NOT NULL DEFAULT 1 COMMENT '1启用/0禁用',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';

-- P.4 权限表
CREATE TABLE sys_permission (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  perm_code     VARCHAR(100)  NOT NULL COMMENT '权限编码 模块:操作',
  perm_name     VARCHAR(100)  NOT NULL COMMENT '权限名称',
  module        VARCHAR(50)   NOT NULL COMMENT '所属模块',
  perm_type     TINYINT       NOT NULL DEFAULT 1 COMMENT '1功能权限/2数据权限',
  UNIQUE KEY uk_perm_code (perm_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='权限表';

-- P.5 角色权限关联表
CREATE TABLE sys_role_permission (
  role_id       INT           NOT NULL,
  permission_id INT           NOT NULL,
  PRIMARY KEY (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色权限关联表';

-- P.6 用户角色关联表
CREATE TABLE sys_user_role (
  user_id       INT           NOT NULL,
  role_id       INT           NOT NULL,
  PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户角色关联表';

-- P.7 委托代理表
CREATE TABLE sys_delegation (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  delegator_id  INT           NOT NULL COMMENT '委托人ID',
  delegatee_id  INT           NOT NULL COMMENT '被委托人ID',
  permission_codes JSON       NOT NULL COMMENT '委托的权限编码列表',
  start_time    DATETIME      NOT NULL COMMENT '委托生效时间',
  end_time      DATETIME      NOT NULL COMMENT '委托到期时间',
  remark        VARCHAR(255)  DEFAULT NULL COMMENT '委托说明',
  status        TINYINT       NOT NULL DEFAULT 1 COMMENT '1有效/0已撤销或已过期',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  KEY idx_delegation_delegator (delegator_id),
  KEY idx_delegation_delegatee (delegatee_id),
  KEY idx_delegation_status_end (status, end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='委托代理表';

-- P.8 系统配置表
CREATE TABLE sys_config (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  config_key    VARCHAR(100)  NOT NULL COMMENT '配置键',
  config_value  VARCHAR(500)  NOT NULL COMMENT '配置值',
  config_desc   VARCHAR(200)  DEFAULT NULL COMMENT '配置说明',
  config_group  VARCHAR(50)   DEFAULT NULL COMMENT '配置分组',
  status        TINYINT       NOT NULL DEFAULT 1 COMMENT '1启用/0禁用',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统配置表';

-- P.9 用户快捷入口配置表
CREATE TABLE sys_user_shortcut (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  user_id       INT           NOT NULL COMMENT '用户ID',
  func_code     VARCHAR(100)  NOT NULL COMMENT '功能编码',
  func_name     VARCHAR(100)  NOT NULL COMMENT '功能显示名称',
  func_icon     VARCHAR(200)  DEFAULT NULL COMMENT '图标标识',
  sort_order    INT           NOT NULL DEFAULT 0 COMMENT '排序号',
  is_default    TINYINT       NOT NULL DEFAULT 0 COMMENT '是否系统默认入口',
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_shortcut_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户快捷入口配置表';

-- P.10 用户个性化配置表
CREATE TABLE sys_user_config (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  user_id       INT           NOT NULL COMMENT '用户ID',
  config_key    VARCHAR(100)  NOT NULL COMMENT '配置项key',
  config_value  VARCHAR(500)  NOT NULL COMMENT '配置值',
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_config (user_id, config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户个性化配置表';

-- P.11 角色数据权限范围表
CREATE TABLE sys_role_data_scope (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  role_id       INT           NOT NULL COMMENT '角色ID',
  scope_type    VARCHAR(20)   NOT NULL COMMENT 'project/dept',
  ref_id        INT           NOT NULL COMMENT '关联的项目ID或部门ID',
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_scope_role_type (role_id, scope_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色数据权限范围表';

-- P.12 系统待办表
CREATE TABLE sys_todo (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  user_id       INT           NOT NULL COMMENT '待办归属用户ID',
  biz_type      VARCHAR(50)   NOT NULL COMMENT '待办关联业务类型',
  biz_id        INT           NOT NULL COMMENT '待办关联业务单据ID',
  title         VARCHAR(200)  NOT NULL COMMENT '待办标题',
  content       VARCHAR(500)  DEFAULT NULL COMMENT '待办内容摘要',
  status        TINYINT       NOT NULL DEFAULT 0 COMMENT '0待处理/1已处理',
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_todo_user_status (user_id, status),
  KEY idx_todo_biz (biz_type, biz_id),
  KEY idx_todo_time (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统待办表';

-- P.13 系统公告表
CREATE TABLE sys_announcement (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  title         VARCHAR(200)  NOT NULL COMMENT '公告标题',
  content       TEXT          NOT NULL COMMENT '公告内容(富文本HTML)',
  type          VARCHAR(30)   NOT NULL COMMENT '公告类型:notice/policy/activity',
  publish_time  DATETIME      DEFAULT NULL COMMENT '发布时间',
  expire_time   DATETIME      DEFAULT NULL COMMENT '过期时间',
  publisher_id  INT           NOT NULL COMMENT '发布人ID',
  status        VARCHAR(30)   NOT NULL DEFAULT 'draft' COMMENT 'draft/published/offline/expired',
  is_top        TINYINT       NOT NULL DEFAULT 0 COMMENT '是否置顶',
  scope         VARCHAR(500)  NOT NULL DEFAULT 'all' COMMENT '可见范围,all或逗号分隔部门ID',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  KEY idx_announce_status (status),
  KEY idx_announce_time (publish_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统公告表';

-- P.14 审计日志表
CREATE TABLE sys_audit_log (
  id            BIGINT        PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
  user_id       INT           NOT NULL COMMENT '操作人ID',
  user_name     VARCHAR(50)   NOT NULL COMMENT '操作人姓名(冗余)',
  operate_type  VARCHAR(30)   NOT NULL COMMENT '操作类型枚举',
  operate_module VARCHAR(50)  NOT NULL COMMENT '操作模块',
  biz_type      VARCHAR(50)   DEFAULT NULL COMMENT '业务类型',
  biz_id        INT           DEFAULT NULL COMMENT '业务对象ID',
  before_data   JSON          DEFAULT NULL COMMENT '操作前数据',
  after_data    JSON          DEFAULT NULL COMMENT '操作后数据',
  ip_address    VARCHAR(45)   DEFAULT NULL COMMENT '操作IP',
  request_id    VARCHAR(36)   DEFAULT NULL COMMENT '请求ID',
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  KEY idx_audit_log_user_time (user_id, created_at),
  KEY idx_audit_log_module_time (operate_module, created_at),
  KEY idx_audit_log_biz (biz_type, biz_id),
  KEY idx_audit_log_time (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='审计日志表';

-- P.15 审批流程定义表
CREATE TABLE sys_flow_def (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  biz_type      VARCHAR(50)   NOT NULL COMMENT '关联业务类型',
  flow_name     VARCHAR(100)  NOT NULL COMMENT '流程名称',
  nodes_json    JSON          NOT NULL COMMENT '审批流程节点配置JSON',
  condition_json JSON         DEFAULT NULL COMMENT '条件分支配置JSON',
  status        TINYINT       NOT NULL DEFAULT 1 COMMENT '1启用/0禁用',
  version       TINYINT       NOT NULL DEFAULT 1 COMMENT '流程版本号',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  UNIQUE KEY uk_flow_biz_version (biz_type, version),
  KEY idx_flow_biz_status (biz_type, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='审批流程定义表';

-- P.16 短信验证码表
CREATE TABLE sms_code (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  phone         VARCHAR(20)   NOT NULL COMMENT '手机号',
  code          VARCHAR(6)    NOT NULL COMMENT '6位验证码',
  expire_time   DATETIME      NOT NULL COMMENT '过期时间',
  used          TINYINT       NOT NULL DEFAULT 0 COMMENT '0未使用/1已使用',
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_sms_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='短信验证码表';

-- P.17 企业邮箱表
CREATE TABLE company_email (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  user_id       INT           NOT NULL COMMENT '用户ID',
  email_address VARCHAR(100)  NOT NULL COMMENT '邮箱地址',
  status        TINYINT       NOT NULL DEFAULT 1 COMMENT '1启用/0禁用',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  UNIQUE KEY uk_email_address (email_address),
  KEY idx_email_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='企业邮箱表';

-- ============================================================
-- 第二部分：业务表 (biz_)
-- ============================================================

-- P.18 项目表
CREATE TABLE biz_project (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  project_no    VARCHAR(20)   NOT NULL COMMENT '项目编号',
  project_name  VARCHAR(200)  NOT NULL COMMENT '项目名称',
  project_alias VARCHAR(100)  DEFAULT NULL COMMENT '项目别名',
  project_type  TINYINT       NOT NULL COMMENT '1虚拟/2实体',
  contract_type VARCHAR(50)   NOT NULL COMMENT '合同类型',
  location      VARCHAR(200)  DEFAULT NULL COMMENT '项目地点',
  amount_with_tax    DECIMAL(14,2) DEFAULT NULL COMMENT '含税金额',
  amount_without_tax DECIMAL(14,2) DEFAULT NULL COMMENT '不含税金额',
  tax_amount    DECIMAL(14,2) DEFAULT NULL COMMENT '税金',
  tax_rate      DECIMAL(5,2)  DEFAULT NULL COMMENT '税率',
  client_name   VARCHAR(200)  DEFAULT NULL COMMENT '甲方信息',
  plan_start_date DATE        DEFAULT NULL COMMENT '计划开始时间',
  plan_end_date DATE          DEFAULT NULL COMMENT '计划结束时间',
  warranty_date DATE          DEFAULT NULL COMMENT '保修期计划时间',
  status        VARCHAR(30)   NOT NULL COMMENT '项目状态',
  manager_id    INT           DEFAULT NULL COMMENT '项目经理ID',
  invest_limit  DECIMAL(14,2) DEFAULT NULL COMMENT '拟投入金额限额(虚拟项目)',
  bid_time      DATE          DEFAULT NULL COMMENT '预计投标时间',
  source_project_id INT       DEFAULT NULL COMMENT '来源虚拟项目ID',
  cost_target_project_id INT  DEFAULT NULL COMMENT '成本下挂目标项目ID',
  remark        TEXT          DEFAULT NULL,
  version       INT           NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  UNIQUE KEY uk_project_no (project_no),
  KEY idx_project_type_status (project_type, status),
  KEY idx_project_manager (manager_id),
  KEY idx_project_creator (creator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='项目表';

-- P.19 合同表
CREATE TABLE biz_contract (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  contract_no   VARCHAR(20)   NOT NULL COMMENT '合同编号',
  contract_name VARCHAR(200)  NOT NULL COMMENT '合同名称',
  contract_type VARCHAR(30)   NOT NULL COMMENT 'income/expense',
  project_id    INT           NOT NULL COMMENT '关联项目ID',
  supplier_id   INT           DEFAULT NULL COMMENT '供应商ID(支出合同)',
  template_id   INT           DEFAULT NULL COMMENT '模板ID',
  amount_with_tax    DECIMAL(14,2) NOT NULL COMMENT '含税金额',
  amount_without_tax DECIMAL(14,2) NOT NULL COMMENT '不含税金额',
  tax_rate      DECIMAL(5,2)  NOT NULL COMMENT '税率',
  tax_amount    DECIMAL(14,2) NOT NULL COMMENT '税额',
  sign_date     DATE          DEFAULT NULL COMMENT '签订日期',
  start_date    DATE          DEFAULT NULL COMMENT '合同开始日期',
  end_date      DATE          DEFAULT NULL COMMENT '合同结束日期',
  party_a       VARCHAR(200)  DEFAULT NULL COMMENT '甲方',
  party_b       VARCHAR(200)  DEFAULT NULL COMMENT '乙方',
  status        VARCHAR(30)   NOT NULL DEFAULT 'draft' COMMENT 'draft/pending/approved/rejected/terminated',
  parent_contract_id INT      DEFAULT NULL COMMENT '原合同ID(补充协议时)',
  purchase_list_id   INT      DEFAULT NULL COMMENT '关联采购清单ID',
  terminate_reason   VARCHAR(500) DEFAULT NULL COMMENT '终止原因',
  terminate_time     DATETIME DEFAULT NULL COMMENT '终止时间',
  terminator_id INT           DEFAULT NULL COMMENT '终止操作人ID',
  remark        TEXT          DEFAULT NULL,
  version       INT           NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  UNIQUE KEY uk_contract_no (contract_no),
  KEY idx_contract_project (project_id),
  KEY idx_contract_supplier (supplier_id),
  KEY idx_contract_type_status (contract_type, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='合同表';

-- P.20 合同物资明细子表
CREATE TABLE biz_contract_material (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  contract_id   INT           NOT NULL COMMENT '关联合同ID',
  material_id   INT           DEFAULT NULL COMMENT '关联材料ID',
  material_name VARCHAR(200)  NOT NULL COMMENT '物资名称',
  spec_model    VARCHAR(200)  DEFAULT NULL COMMENT '规格型号',
  unit          VARCHAR(20)   NOT NULL COMMENT '单位',
  quantity      DECIMAL(14,4) NOT NULL COMMENT '数量',
  unit_price    DECIMAL(14,2) NOT NULL COMMENT '单价(含税)',
  subtotal      DECIMAL(14,2) NOT NULL COMMENT '小计金额',
  list_item_id  INT           DEFAULT NULL COMMENT '关联采购清单明细ID',
  remark        VARCHAR(500)  DEFAULT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_cm_contract (contract_id),
  KEY idx_cm_material (material_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='合同物资明细子表';

-- P.21 合同版本快照表
CREATE TABLE biz_contract_version (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  contract_id   INT           NOT NULL COMMENT '原合同ID',
  version_no    TINYINT       NOT NULL DEFAULT 1 COMMENT '版本号',
  supplement_id INT           DEFAULT NULL COMMENT '触发版本变更的补充协议ID',
  snapshot_json JSON          NOT NULL COMMENT '合同关键字段的完整JSON快照',
  change_summary VARCHAR(500) DEFAULT NULL COMMENT '变更摘要说明',
  created_by    INT           NOT NULL COMMENT '快照创建人',
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_cv_contract_version (contract_id, version_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='合同版本快照表';

-- P.22 合同付款计划子表
CREATE TABLE biz_contract_payment_plan (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  contract_id   INT           NOT NULL COMMENT '关联合同ID',
  term_no       INT           NOT NULL COMMENT '期次序号',
  payment_stage VARCHAR(50)   NOT NULL COMMENT '付款阶段:advance/progress/final/other',
  payment_ratio DECIMAL(5,2)  NOT NULL COMMENT '付款比例(%)',
  payment_amount DECIMAL(14,2) NOT NULL COMMENT '付款金额(含税)',
  plan_payment_date DATE      NOT NULL COMMENT '计划付款日期',
  actual_payment_date DATE    DEFAULT NULL COMMENT '实际付款日期',
  status        VARCHAR(30)   NOT NULL DEFAULT 'pending' COMMENT 'pending/paid',
  remark        VARCHAR(255)  DEFAULT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_cpp_contract (contract_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='合同付款计划子表';

-- P.23 供应商表
CREATE TABLE biz_supplier (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  supplier_name VARCHAR(200)  NOT NULL COMMENT '供应商名称',
  contact_person VARCHAR(50)  DEFAULT NULL COMMENT '联系人',
  contact_phone VARCHAR(20)   DEFAULT NULL COMMENT '联系电话',
  address       VARCHAR(300)  DEFAULT NULL COMMENT '地址',
  bank_name     VARCHAR(200)  DEFAULT NULL COMMENT '开户行',
  bank_account  VARCHAR(50)   DEFAULT NULL COMMENT '银行账号',
  tax_no        VARCHAR(50)   DEFAULT NULL COMMENT '纳税人识别号',
  status        VARCHAR(30)   NOT NULL DEFAULT 'active' COMMENT 'active/inactive',
  remark        VARCHAR(500)  DEFAULT NULL,
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  UNIQUE KEY uk_supplier_name (supplier_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='供应商表';

-- P.24 材料基础信息表
CREATE TABLE biz_material_base (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  material_code VARCHAR(20)   NOT NULL COMMENT '材料编码(M+6位)',
  material_name VARCHAR(200)  NOT NULL COMMENT '材料名称',
  spec_model    VARCHAR(200)  DEFAULT NULL COMMENT '规格型号',
  unit          VARCHAR(20)   NOT NULL COMMENT '计量单位',
  category      VARCHAR(50)   DEFAULT NULL COMMENT '材料分类',
  base_price    DECIMAL(14,2) DEFAULT NULL COMMENT '当前基准价',
  status        VARCHAR(30)   NOT NULL DEFAULT 'active' COMMENT 'active/inactive',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  UNIQUE KEY uk_material_code (material_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='材料基础信息表';

-- P.25 材料基准价历史表
CREATE TABLE biz_material_base_price (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  material_id   INT           NOT NULL COMMENT '材料ID',
  price         DECIMAL(14,2) NOT NULL COMMENT '基准价',
  effective_date DATE         NOT NULL COMMENT '生效日期',
  source_type   VARCHAR(30)   NOT NULL COMMENT '来源:manual/contract_auto',
  source_id     INT           DEFAULT NULL COMMENT '来源ID(合同ID等)',
  status        VARCHAR(30)   NOT NULL DEFAULT 'active' COMMENT 'active/inactive',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_mbp_material (material_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='材料基准价历史表';

-- P.26 材料价格变动记录表
CREATE TABLE biz_material_price_history (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  material_id   INT           NOT NULL COMMENT '材料ID',
  before_price  DECIMAL(14,2) NOT NULL COMMENT '变更前价格',
  after_price   DECIMAL(14,2) NOT NULL COMMENT '变更后价格',
  change_reason VARCHAR(200)  DEFAULT NULL COMMENT '变更原因',
  operator_id   INT           NOT NULL COMMENT '操作人ID',
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_mph_material (material_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='材料价格变动记录表';

-- P.27 采购清单表
CREATE TABLE biz_purchase_list (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  list_no       VARCHAR(20)   NOT NULL COMMENT '采购清单编号',
  project_id    INT           NOT NULL COMMENT '关联项目ID',
  total_amount  DECIMAL(14,2) DEFAULT NULL COMMENT '总金额',
  status        VARCHAR(30)   NOT NULL DEFAULT 'draft' COMMENT 'draft/pending/approved/rejected',
  remark        VARCHAR(500)  DEFAULT NULL,
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  UNIQUE KEY uk_list_no (list_no),
  KEY idx_pl_project (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='采购清单表';

-- P.28 采购清单明细子表
CREATE TABLE biz_purchase_list_item (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  list_id       INT           NOT NULL COMMENT '关联采购清单ID',
  material_id   INT           DEFAULT NULL COMMENT '关联材料ID',
  material_name VARCHAR(200)  NOT NULL COMMENT '物资名称',
  spec_model    VARCHAR(200)  DEFAULT NULL COMMENT '规格型号',
  unit          VARCHAR(20)   NOT NULL COMMENT '单位',
  quantity      DECIMAL(14,4) NOT NULL COMMENT '计划数量',
  estimated_price DECIMAL(14,2) DEFAULT NULL COMMENT '预估单价',
  subtotal      DECIMAL(14,2) DEFAULT NULL COMMENT '小计',
  remark        VARCHAR(500)  DEFAULT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_pli_list (list_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='采购清单明细子表';

-- P.29 零星采购表
CREATE TABLE biz_spot_purchase (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  purchase_no   VARCHAR(20)   NOT NULL COMMENT '零星采购编号',
  project_id    INT           NOT NULL COMMENT '关联项目ID',
  item_name     VARCHAR(200)  NOT NULL COMMENT '采购物品名称',
  spec_model    VARCHAR(200)  DEFAULT NULL COMMENT '规格型号',
  quantity      DECIMAL(14,4) NOT NULL COMMENT '数量',
  unit_price    DECIMAL(14,2) NOT NULL COMMENT '单价',
  amount        DECIMAL(14,2) NOT NULL COMMENT '金额',
  supplier_name VARCHAR(200)  DEFAULT NULL COMMENT '供应商名称',
  status        VARCHAR(30)   NOT NULL DEFAULT 'draft' COMMENT 'draft/pending/approved/rejected',
  remark        VARCHAR(500)  DEFAULT NULL,
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  UNIQUE KEY uk_spot_no (purchase_no),
  KEY idx_spot_project (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='零星采购表';

-- P.30 入库单表
CREATE TABLE biz_inbound_order (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  inbound_no    VARCHAR(20)   NOT NULL COMMENT '入库编号',
  contract_id   INT           NOT NULL COMMENT '关联支出合同ID',
  project_id    INT           NOT NULL COMMENT '关联项目ID',
  warehouse     VARCHAR(100)  DEFAULT NULL COMMENT '入库仓库',
  inbound_date  DATE          DEFAULT NULL COMMENT '入库日期',
  status        VARCHAR(30)   NOT NULL DEFAULT 'draft' COMMENT 'draft/pending/approved/rejected',
  remark        VARCHAR(500)  DEFAULT NULL,
  version       INT           NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  UNIQUE KEY uk_inbound_no (inbound_no),
  KEY idx_ib_contract (contract_id),
  KEY idx_ib_project (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='入库单表';

-- P.31 入库单明细子表
CREATE TABLE biz_inbound_order_item (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  inbound_id    INT           NOT NULL COMMENT '关联入库单ID',
  material_id   INT           DEFAULT NULL COMMENT '关联材料ID',
  material_name VARCHAR(200)  NOT NULL COMMENT '物资名称',
  spec_model    VARCHAR(200)  DEFAULT NULL COMMENT '规格型号',
  unit          VARCHAR(20)   NOT NULL COMMENT '单位',
  quantity      DECIMAL(14,4) NOT NULL COMMENT '入库数量',
  unit_price    DECIMAL(14,2) NOT NULL COMMENT '单价',
  subtotal      DECIMAL(14,2) NOT NULL COMMENT '小计',
  contract_material_id INT    DEFAULT NULL COMMENT '关联合同物资明细ID',
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_ibi_inbound (inbound_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='入库单明细子表';

-- P.32 出库单表
CREATE TABLE biz_outbound_order (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  outbound_no   VARCHAR(20)   NOT NULL COMMENT '出库编号',
  project_id    INT           NOT NULL COMMENT '关联项目ID',
  outbound_type VARCHAR(30)   NOT NULL COMMENT '出库类型:project_site/purchase',
  outbound_date DATE          DEFAULT NULL COMMENT '出库日期',
  status        VARCHAR(30)   NOT NULL DEFAULT 'draft' COMMENT 'draft/pending/approved/rejected',
  remark        VARCHAR(500)  DEFAULT NULL,
  version       INT           NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  UNIQUE KEY uk_outbound_no (outbound_no),
  KEY idx_ob_project (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='出库单表';

-- P.33 出库单明细子表
CREATE TABLE biz_outbound_order_item (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  outbound_id   INT           NOT NULL COMMENT '关联出库单ID',
  material_id   INT           NOT NULL COMMENT '关联材料ID',
  material_name VARCHAR(200)  NOT NULL COMMENT '物资名称',
  unit          VARCHAR(20)   NOT NULL COMMENT '单位',
  quantity      DECIMAL(14,4) NOT NULL COMMENT '出库数量',
  avg_price     DECIMAL(14,2) NOT NULL COMMENT '出库时加权平均单价',
  subtotal      DECIMAL(14,2) NOT NULL COMMENT '出库成本小计',
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_obi_outbound (outbound_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='出库单明细子表';

-- P.34 退库单表
CREATE TABLE biz_return_order (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  return_no     VARCHAR(20)   NOT NULL COMMENT '退库编号',
  project_id    INT           NOT NULL COMMENT '关联项目ID',
  dispose_method VARCHAR(30)  NOT NULL COMMENT '处置方式:on_site/return_factory/to_warehouse/transfer',
  target_project_id INT       DEFAULT NULL COMMENT '目标项目ID(项目间调拨时)',
  return_date   DATE          DEFAULT NULL COMMENT '退库日期',
  status        VARCHAR(30)   NOT NULL DEFAULT 'draft' COMMENT 'draft/pending/approved/rejected',
  remark        VARCHAR(500)  DEFAULT NULL,
  version       INT           NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  UNIQUE KEY uk_return_no (return_no),
  KEY idx_rt_project (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='退库单表';

-- P.35 退库单明细子表
CREATE TABLE biz_return_order_item (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  return_id     INT           NOT NULL COMMENT '关联退库单ID',
  material_id   INT           NOT NULL COMMENT '关联材料ID',
  material_name VARCHAR(200)  NOT NULL COMMENT '物资名称',
  unit          VARCHAR(20)   NOT NULL COMMENT '单位',
  quantity      DECIMAL(14,4) NOT NULL COMMENT '退库数量',
  unit_price    DECIMAL(14,2) NOT NULL COMMENT '单价',
  subtotal      DECIMAL(14,2) NOT NULL COMMENT '小计',
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_rti_return (return_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='退库单明细子表';

-- P.36 库存表
CREATE TABLE biz_inventory (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  project_id    INT           NOT NULL COMMENT '项目ID',
  material_id   INT           NOT NULL COMMENT '材料ID',
  material_name VARCHAR(200)  NOT NULL COMMENT '物资名称(冗余)',
  unit          VARCHAR(20)   NOT NULL COMMENT '单位',
  current_quantity DECIMAL(14,4) NOT NULL DEFAULT 0 COMMENT '当前库存数量',
  avg_price     DECIMAL(14,2) NOT NULL DEFAULT 0 COMMENT '加权平均单价',
  total_amount  DECIMAL(14,2) NOT NULL DEFAULT 0 COMMENT '库存总金额',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  UNIQUE KEY uk_inventory_project_material (project_id, material_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='库存表';

-- P.37 盘点单表
CREATE TABLE biz_inventory_check (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  check_no      VARCHAR(20)   NOT NULL COMMENT '盘点编号',
  project_id    INT           NOT NULL COMMENT '项目ID',
  check_date    DATE          NOT NULL COMMENT '盘点日期',
  diff_amount   DECIMAL(14,2) DEFAULT NULL COMMENT '差异金额',
  diff_ratio    DECIMAL(5,2)  DEFAULT NULL COMMENT '差异比例(%)',
  is_over_threshold TINYINT   NOT NULL DEFAULT 0 COMMENT '是否超阈值(>10%)',
  status        VARCHAR(30)   NOT NULL DEFAULT 'draft' COMMENT 'draft/pending/approved/rejected/closed',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  UNIQUE KEY uk_check_no (check_no),
  KEY idx_ic_project (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='盘点单表';

-- P.38 盘点明细子表
CREATE TABLE biz_inventory_check_item (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  check_id      INT           NOT NULL COMMENT '关联盘点单ID',
  material_id   INT           NOT NULL COMMENT '材料ID',
  system_quantity DECIMAL(14,4) NOT NULL COMMENT '系统数量',
  actual_quantity DECIMAL(14,4) NOT NULL COMMENT '实盘数量',
  diff_quantity DECIMAL(14,4) NOT NULL COMMENT '差异数量',
  avg_price     DECIMAL(14,2) NOT NULL COMMENT '加权平均单价',
  diff_amount   DECIMAL(14,2) NOT NULL COMMENT '差异金额',
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_ici_check (check_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='盘点明细子表';

-- P.39 库存变动记录表
CREATE TABLE biz_inventory_record (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  project_id    INT           NOT NULL COMMENT '项目ID',
  material_id   INT           NOT NULL COMMENT '材料ID',
  before_quantity DECIMAL(14,4) NOT NULL COMMENT '变动前数量',
  change_quantity DECIMAL(14,4) NOT NULL COMMENT '变动数量(正入负出)',
  after_quantity DECIMAL(14,4) NOT NULL COMMENT '变动后数量',
  before_avg_price DECIMAL(14,2) NOT NULL COMMENT '变动前均价',
  after_avg_price  DECIMAL(14,2) NOT NULL COMMENT '变动后均价',
  change_type   VARCHAR(30)   NOT NULL COMMENT '变动类型:inbound/outbound/return/check_adjust',
  biz_type      VARCHAR(50)   NOT NULL COMMENT '业务类型',
  biz_id        INT           NOT NULL COMMENT '业务单据ID',
  operate_time  DATETIME      NOT NULL COMMENT '操作时间',
  operator_id   INT           NOT NULL COMMENT '操作人ID',
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_ir_project_material (project_id, material_id),
  KEY idx_ir_time (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='库存变动记录表';

-- P.40 甘特图任务表
CREATE TABLE biz_gantt_task (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  project_id    INT           NOT NULL COMMENT '关联项目ID',
  parent_id     INT           NOT NULL DEFAULT 0 COMMENT '父任务ID,0为里程碑',
  task_name     VARCHAR(200)  NOT NULL COMMENT '任务名称',
  task_type     TINYINT       NOT NULL COMMENT '1里程碑/2任务',
  plan_start_date DATE        DEFAULT NULL COMMENT '计划开始日期',
  plan_end_date DATE          DEFAULT NULL COMMENT '计划结束日期',
  actual_start_date DATE      DEFAULT NULL COMMENT '实际开始日期',
  actual_end_date DATE        DEFAULT NULL COMMENT '实际结束日期',
  progress_pct  DECIMAL(5,2)  NOT NULL DEFAULT 0 COMMENT '进度百分比',
  dependency_type VARCHAR(10) DEFAULT NULL COMMENT '依赖关系类型:FS/SS/FF/SF',
  dependency_task_id INT      DEFAULT NULL COMMENT '依赖任务ID',
  sort_order    INT           NOT NULL DEFAULT 0 COMMENT '排序号',
  status        VARCHAR(30)   NOT NULL DEFAULT 'draft' COMMENT 'draft/pending/approved/locked',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  KEY idx_gantt_project (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='甘特图任务表';

-- P.41 进度描述记录表
CREATE TABLE biz_gantt_progress_desc (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  task_id       INT           NOT NULL COMMENT '关联甘特图任务ID',
  content       TEXT          NOT NULL COMMENT '进度描述内容',
  progress_pct  DECIMAL(5,2)  NOT NULL COMMENT '填报时进度百分比',
  current_output DECIMAL(14,2) DEFAULT NULL COMMENT '当期产值',
  creator_id    INT           NOT NULL COMMENT '填报人ID',
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_gpd_task (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='进度描述记录表';

-- P.42 变更管理主表
CREATE TABLE biz_change_order (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  change_no     VARCHAR(20)   NOT NULL COMMENT '变更编号',
  change_type   VARCHAR(30)   NOT NULL COMMENT '变更类型:visa/owner_change/overage/labor_visa',
  project_id    INT           NOT NULL COMMENT '关联项目ID',
  contract_id   INT           DEFAULT NULL COMMENT '关联合同ID',
  title         VARCHAR(200)  NOT NULL COMMENT '变更标题',
  description   TEXT          DEFAULT NULL COMMENT '变更描述',
  total_amount  DECIMAL(14,2) DEFAULT NULL COMMENT '变更总金额',
  status        VARCHAR(30)   NOT NULL DEFAULT 'draft' COMMENT 'draft/pending/approved/rejected',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  UNIQUE KEY uk_change_no (change_no),
  KEY idx_co_project (project_id),
  KEY idx_co_contract (contract_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='变更管理主表';

-- P.43 变更明细子表
CREATE TABLE biz_change_detail (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  change_id     INT           NOT NULL COMMENT '关联变更单ID',
  item_name     VARCHAR(200)  NOT NULL COMMENT '变更项目名称',
  spec_model    VARCHAR(200)  DEFAULT NULL COMMENT '规格型号',
  unit          VARCHAR(20)   DEFAULT NULL COMMENT '单位',
  plan_quantity DECIMAL(14,4) DEFAULT NULL COMMENT '计划数量',
  actual_quantity DECIMAL(14,4) DEFAULT NULL COMMENT '实际数量',
  diff_quantity DECIMAL(14,4) DEFAULT NULL COMMENT '差异数量',
  unit_price    DECIMAL(14,2) DEFAULT NULL COMMENT '单价',
  subtotal      DECIMAL(14,2) DEFAULT NULL COMMENT '小计',
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_cd_change (change_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='变更明细子表';

-- P.44 对账单表
CREATE TABLE biz_statement (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  statement_no  VARCHAR(20)   NOT NULL COMMENT '对账单编号',
  project_id    INT           NOT NULL COMMENT '项目ID',
  contract_id   INT           NOT NULL COMMENT '收入合同ID',
  period        VARCHAR(10)   NOT NULL COMMENT '账期(YYYYMM)',
  contract_amount DECIMAL(14,2) NOT NULL COMMENT '合同含税金额',
  progress_ratio DECIMAL(5,2) DEFAULT NULL COMMENT '进度比例(%)',
  current_output DECIMAL(14,2) DEFAULT NULL COMMENT '当期产值',
  cumulative_output DECIMAL(14,2) DEFAULT NULL COMMENT '累计产值',
  current_collection DECIMAL(14,2) DEFAULT NULL COMMENT '当期收款',
  cumulative_collection DECIMAL(14,2) DEFAULT NULL COMMENT '累计收款',
  status        VARCHAR(30)   NOT NULL DEFAULT 'draft' COMMENT 'draft/pending/approved/rejected/voided',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  UNIQUE KEY uk_statement_no (statement_no),
  KEY idx_st_project (project_id),
  KEY idx_st_contract (contract_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='对账单表';

-- P.45 收入合同拆分表
CREATE TABLE biz_income_split (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  contract_id   INT           NOT NULL COMMENT '收入合同ID',
  project_id    INT           NOT NULL COMMENT '项目ID',
  total_split_amount DECIMAL(14,2) NOT NULL COMMENT '拆分总金额',
  status        VARCHAR(30)   NOT NULL DEFAULT 'draft' COMMENT 'draft/pending/approved/rejected',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  KEY idx_is_contract (contract_id),
  KEY idx_is_project (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收入合同拆分表';

-- P.46 收入拆分明细子表
CREATE TABLE biz_income_split_item (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  split_id      INT           NOT NULL COMMENT '关联拆分主表ID',
  gantt_task_id INT           NOT NULL COMMENT '关联甘特图任务ID',
  milestone_name VARCHAR(200) DEFAULT NULL COMMENT '里程碑名称',
  task_name     VARCHAR(200)  NOT NULL COMMENT '任务名称',
  task_amount   DECIMAL(14,2) NOT NULL COMMENT '任务合同金额',
  progress_pct  DECIMAL(5,2)  NOT NULL DEFAULT 0 COMMENT '进度百分比',
  current_output DECIMAL(14,2) NOT NULL DEFAULT 0 COMMENT '当期产值',
  cumulative_output DECIMAL(14,2) NOT NULL DEFAULT 0 COMMENT '累计产值',
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_isi_split (split_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收入拆分明细子表';

-- P.47 付款申请表
CREATE TABLE biz_payment_apply (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  payment_no    VARCHAR(20)   NOT NULL COMMENT '付款编号',
  payment_type  VARCHAR(20)   NOT NULL COMMENT 'labor/material',
  project_id    INT           NOT NULL COMMENT '关联项目ID',
  contract_id   INT           NOT NULL COMMENT '关联支出合同ID',
  statement_id  INT           DEFAULT NULL COMMENT '关联对账单ID(人工费付款必填)',
  amount        DECIMAL(14,2) NOT NULL COMMENT '付款金额',
  payee_name    VARCHAR(200)  NOT NULL COMMENT '收款方名称',
  payee_bank    VARCHAR(200)  DEFAULT NULL COMMENT '收款银行',
  payee_account VARCHAR(50)   DEFAULT NULL COMMENT '收款账号',
  payment_method VARCHAR(50)  DEFAULT NULL COMMENT '付款方式',
  status        VARCHAR(30)   NOT NULL DEFAULT 'draft' COMMENT 'draft/pending/approved/rejected/confirmed/collected/collect_failed',
  confirm_time  DATETIME      DEFAULT NULL COMMENT '确认打款时间',
  confirmer_id  INT           DEFAULT NULL COMMENT '确认人ID',
  confirm_remark VARCHAR(200) DEFAULT NULL COMMENT '确认备注',
  remark        TEXT          DEFAULT NULL,
  version       INT           NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  UNIQUE KEY uk_payment_no (payment_no),
  KEY idx_pa_project (project_id),
  KEY idx_pa_contract_status (contract_id, status),
  KEY idx_pa_type_status (payment_type, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='付款申请表';

-- P.48 收款登记表
CREATE TABLE biz_payment_receipt (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  receipt_no    VARCHAR(20)   NOT NULL COMMENT '收款编号',
  project_id    INT           NOT NULL COMMENT '项目ID',
  contract_id   INT           NOT NULL COMMENT '收入合同ID',
  amount        DECIMAL(14,2) NOT NULL COMMENT '收款金额',
  receipt_date  DATE          NOT NULL COMMENT '收款日期',
  receipt_method VARCHAR(50)  DEFAULT NULL COMMENT '收款方式',
  remark        VARCHAR(500)  DEFAULT NULL,
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  UNIQUE KEY uk_receipt_no (receipt_no),
  KEY idx_pr_project (project_id),
  KEY idx_pr_contract (contract_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收款登记表';

-- P.49 付款与入库关联中间表
CREATE TABLE biz_payment_inbound_rel (
  payment_id    INT           NOT NULL COMMENT '付款申请ID',
  inbound_id    INT           NOT NULL COMMENT '入库单ID',
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (payment_id, inbound_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='付款与入库关联中间表';

-- P.50 成本台账表
CREATE TABLE biz_cost_ledger (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  project_id    INT           NOT NULL COMMENT '项目ID',
  cost_type     VARCHAR(50)   NOT NULL COMMENT '成本一级科目',
  cost_subtype  VARCHAR(50)   NOT NULL COMMENT '成本二级科目',
  amount        DECIMAL(14,2) NOT NULL COMMENT '归集金额',
  biz_type      VARCHAR(50)   NOT NULL COMMENT '业务来源类型',
  biz_id        INT           NOT NULL COMMENT '业务来源ID',
  collect_time  DATETIME      NOT NULL COMMENT '归集时间',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_cl_project (project_id),
  KEY idx_cl_type (cost_type, cost_subtype)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='成本台账表';

-- P.51 发票管理表
CREATE TABLE biz_invoice (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  invoice_no    VARCHAR(50)   NOT NULL COMMENT '发票号码',
  invoice_type  VARCHAR(30)   NOT NULL COMMENT '发票类型:special/normal/other',
  amount        DECIMAL(14,2) NOT NULL COMMENT '发票金额',
  tax_rate      DECIMAL(5,2)  DEFAULT NULL COMMENT '税率',
  tax_amount    DECIMAL(14,2) DEFAULT NULL COMMENT '税额',
  invoice_date  DATE          NOT NULL COMMENT '开票日期',
  invoice_party VARCHAR(200)  DEFAULT NULL COMMENT '开票方',
  biz_type      VARCHAR(50)   NOT NULL COMMENT '业务类型',
  biz_id        INT           NOT NULL COMMENT '关联业务单据ID',
  attachment_id INT           DEFAULT NULL COMMENT '发票扫描件附件ID',
  is_certified  TINYINT       NOT NULL DEFAULT 0 COMMENT '是否已认证(专票)',
  certified_date DATE         DEFAULT NULL COMMENT '认证日期',
  status        VARCHAR(30)   NOT NULL DEFAULT 'active' COMMENT 'active/voided',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  KEY idx_inv_biz (biz_type, biz_id),
  KEY idx_inv_date (invoice_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='发票管理表';

-- P.52 报销单表
CREATE TABLE biz_reimburse (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  reimburse_no  VARCHAR(20)   NOT NULL COMMENT '报销编号',
  reimburse_type VARCHAR(50)  NOT NULL COMMENT '报销类型',
  amount        DECIMAL(14,2) NOT NULL COMMENT '报销金额',
  dept_id       INT           NOT NULL COMMENT '所属部门ID',
  project_id    INT           DEFAULT NULL COMMENT '关联项目ID(项目报销时)',
  description   TEXT          DEFAULT NULL COMMENT '报销说明',
  status        VARCHAR(30)   NOT NULL DEFAULT 'draft' COMMENT 'draft/pending/approved/rejected/confirmed',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  UNIQUE KEY uk_reimburse_no (reimburse_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报销单表';

-- P.53 工资表
CREATE TABLE biz_salary (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  user_id       INT           NOT NULL COMMENT '员工ID',
  salary_month  VARCHAR(10)   NOT NULL COMMENT '工资月份(YYYYMM)',
  base_salary   DECIMAL(14,2) NOT NULL DEFAULT 0 COMMENT '基本工资',
  position_salary DECIMAL(14,2) NOT NULL DEFAULT 0 COMMENT '岗位工资',
  performance   DECIMAL(14,2) NOT NULL DEFAULT 0 COMMENT '绩效工资',
  allowance     DECIMAL(14,2) NOT NULL DEFAULT 0 COMMENT '补贴',
  bonus         DECIMAL(14,2) NOT NULL DEFAULT 0 COMMENT '奖金',
  deduction     DECIMAL(14,2) NOT NULL DEFAULT 0 COMMENT '扣款',
  social_insurance DECIMAL(14,2) NOT NULL DEFAULT 0 COMMENT '社保个人扣款',
  tax           DECIMAL(14,2) NOT NULL DEFAULT 0 COMMENT '个人所得税',
  net_salary    DECIMAL(14,2) NOT NULL DEFAULT 0 COMMENT '实发工资',
  status        VARCHAR(30)   NOT NULL DEFAULT 'draft' COMMENT 'draft/pending/approved/rejected',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  UNIQUE KEY uk_salary_user_month (user_id, salary_month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工资表';

-- P.54 薪资配置表
CREATE TABLE biz_salary_config (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  user_id       INT           NOT NULL COMMENT '员工ID',
  base_salary   DECIMAL(14,2) NOT NULL COMMENT '基本工资',
  position_salary DECIMAL(14,2) NOT NULL DEFAULT 0 COMMENT '岗位工资',
  performance_base DECIMAL(14,2) NOT NULL DEFAULT 0 COMMENT '绩效基数',
  allowance     DECIMAL(14,2) NOT NULL DEFAULT 0 COMMENT '补贴',
  effective_date DATE         NOT NULL COMMENT '生效日期',
  status        VARCHAR(30)   NOT NULL DEFAULT 'active' COMMENT 'active/inactive',
  remark        VARCHAR(255)  DEFAULT NULL,
  version       INT           NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  KEY idx_sc_user (user_id),
  KEY idx_sc_status_date (status, effective_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='薪资配置表';

-- P.55 社保配置表
CREATE TABLE biz_social_insurance_config (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  user_id       INT           NOT NULL COMMENT '员工ID',
  pension_base  DECIMAL(14,2) NOT NULL COMMENT '养老保险基数',
  pension_ratio_personal DECIMAL(5,2) NOT NULL COMMENT '养老个人比例(%)',
  pension_ratio_company  DECIMAL(5,2) NOT NULL COMMENT '养老企业比例(%)',
  medical_base  DECIMAL(14,2) NOT NULL COMMENT '医疗保险基数',
  medical_ratio_personal DECIMAL(5,2) NOT NULL COMMENT '医疗个人比例(%)',
  medical_ratio_company  DECIMAL(5,2) NOT NULL COMMENT '医疗企业比例(%)',
  unemployment_base DECIMAL(14,2) NOT NULL COMMENT '失业保险基数',
  unemployment_ratio DECIMAL(5,2) NOT NULL COMMENT '失业个人比例(%)',
  housing_fund_base DECIMAL(14,2) NOT NULL COMMENT '住房公积金基数',
  housing_fund_ratio DECIMAL(5,2) NOT NULL COMMENT '公积金个人比例(%)',
  effective_date DATE         NOT NULL COMMENT '生效日期',
  status        VARCHAR(30)   NOT NULL DEFAULT 'active' COMMENT 'active/inactive',
  version       INT           NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  KEY idx_sic_user (user_id),
  KEY idx_sic_status_date (status, effective_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='社保配置表';

-- P.56 个税税率表
CREATE TABLE cfg_tax_rate_table (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  level         INT           NOT NULL COMMENT '级数(1~7)',
  min_amount    DECIMAL(14,2) NOT NULL COMMENT '累计应纳税所得额下限(含)',
  max_amount    DECIMAL(14,2) DEFAULT NULL COMMENT '上限(不含),最高级NULL',
  tax_rate      DECIMAL(5,2)  NOT NULL COMMENT '税率(%)',
  quick_deduction DECIMAL(14,2) NOT NULL COMMENT '速算扣除数',
  effective_date DATE         NOT NULL COMMENT '生效日期',
  status        VARCHAR(30)   NOT NULL DEFAULT 'active' COMMENT 'active/inactive',
  creator_id    INT           NOT NULL COMMENT '创建人ID',
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='个税税率表';

-- 初始化7级税率数据(2024年税率)
INSERT INTO cfg_tax_rate_table (level, min_amount, max_amount, tax_rate, quick_deduction, effective_date, status, creator_id) VALUES
(1,      0.00,   36000.00,  3.00,      0.00, '2024-01-01', 'active', 1),
(2,  36000.00,  144000.00, 10.00,   2520.00, '2024-01-01', 'active', 1),
(3, 144000.00,  300000.00, 20.00,  16920.00, '2024-01-01', 'active', 1),
(4, 300000.00,  420000.00, 25.00,  31920.00, '2024-01-01', 'active', 1),
(5, 420000.00,  660000.00, 30.00,  52920.00, '2024-01-01', 'active', 1),
(6, 660000.00,  960000.00, 35.00,  85920.00, '2024-01-01', 'active', 1),
(7, 960000.00,      NULL,  45.00, 181920.00, '2024-01-01', 'active', 1);

-- P.57 人员合同表
CREATE TABLE biz_hr_contract (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  user_id       INT           NOT NULL COMMENT '员工ID',
  contract_type VARCHAR(30)   NOT NULL COMMENT '合同类型:fixed/indefinite/internship',
  start_date    DATE          NOT NULL COMMENT '合同开始日期',
  end_date      DATE          DEFAULT NULL COMMENT '合同结束日期(无固定期限为NULL)',
  status        VARCHAR(30)   NOT NULL DEFAULT 'active' COMMENT 'active/expired/renewed',
  renewal_id    INT           DEFAULT NULL COMMENT '续签合同ID',
  contract_file_id INT        DEFAULT NULL COMMENT '合同扫描件附件ID',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  KEY idx_hrc_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='人员合同表';

-- P.58 资质表
CREATE TABLE biz_hr_certificate (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  cert_type     VARCHAR(30)   NOT NULL COMMENT '资质类型:company/personal',
  user_id       INT           DEFAULT NULL COMMENT '持证人ID(个人资质)',
  cert_name     VARCHAR(200)  NOT NULL COMMENT '证书名称',
  cert_category VARCHAR(100)  DEFAULT NULL COMMENT '证书类别',
  cert_no       VARCHAR(100)  DEFAULT NULL COMMENT '证书编号',
  issue_date    DATE          DEFAULT NULL COMMENT '发证日期',
  expire_date   DATE          DEFAULT NULL COMMENT '到期日期',
  attachment_id INT           DEFAULT NULL COMMENT '证书扫描件附件ID',
  warn_status   VARCHAR(30)   NOT NULL DEFAULT 'normal' COMMENT '预警状态:normal/warning/expired',
  status        VARCHAR(30)   NOT NULL DEFAULT 'active' COMMENT 'active/inactive',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  KEY idx_cert_user (user_id),
  KEY idx_cert_expire (expire_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='资质表';

-- P.59 入职申请表
CREATE TABLE biz_hr_entry (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  entry_no      VARCHAR(20)   NOT NULL COMMENT '入职申请编号',
  applicant_name VARCHAR(50)  NOT NULL COMMENT '申请人姓名',
  phone         VARCHAR(20)   NOT NULL COMMENT '手机号',
  dept_id       INT           NOT NULL COMMENT '入职部门ID',
  position      VARCHAR(100)  DEFAULT NULL COMMENT '入职职位',
  entry_date    DATE          NOT NULL COMMENT '入职日期',
  education     VARCHAR(50)   DEFAULT NULL COMMENT '学历',
  work_years    INT           DEFAULT NULL COMMENT '工作年限',
  id_card_no    VARCHAR(100)  DEFAULT NULL COMMENT '身份证号(AES-256加密存储)',
  status        VARCHAR(30)   NOT NULL DEFAULT 'draft' COMMENT 'draft/pending/approved/rejected',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  UNIQUE KEY uk_entry_no (entry_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='入职申请表';

-- P.60 离职申请表
CREATE TABLE biz_hr_resign (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  resign_no     VARCHAR(20)   NOT NULL COMMENT '离职申请编号',
  user_id       INT           NOT NULL COMMENT '离职员工ID',
  resign_type   VARCHAR(30)   NOT NULL COMMENT '离职类型:voluntary/involuntary',
  resign_date   DATE          NOT NULL COMMENT '离职日期',
  resign_reason TEXT          DEFAULT NULL COMMENT '离职原因',
  handover_status VARCHAR(30) NOT NULL DEFAULT 'pending' COMMENT '交接状态:pending/completed',
  handover_to   INT           DEFAULT NULL COMMENT '交接人ID',
  status        VARCHAR(30)   NOT NULL DEFAULT 'draft' COMMENT 'draft/pending/approved/rejected',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  UNIQUE KEY uk_resign_no (resign_no),
  KEY idx_resign_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='离职申请表';

-- P.61 资产移交表
CREATE TABLE biz_hr_asset_transfer (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  transfer_no   VARCHAR(20)   NOT NULL COMMENT '移交编号',
  asset_type    VARCHAR(50)   NOT NULL COMMENT '资产类型',
  asset_name    VARCHAR(200)  NOT NULL COMMENT '资产名称',
  asset_code    VARCHAR(50)   DEFAULT NULL COMMENT '资产编码',
  from_user_id  INT           NOT NULL COMMENT '移交人ID',
  to_user_id    INT           NOT NULL COMMENT '接收人ID',
  project_id    INT           DEFAULT NULL COMMENT '关联项目ID',
  transfer_date DATE          NOT NULL COMMENT '移交日期',
  quantity      INT           NOT NULL DEFAULT 1 COMMENT '数量',
  status        VARCHAR(30)   NOT NULL DEFAULT 'pending' COMMENT 'pending/completed',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  UNIQUE KEY uk_transfer_no (transfer_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='资产移交表';

-- P.62 完工验收表
CREATE TABLE biz_completion_finish (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  project_id    INT           NOT NULL COMMENT '项目ID',
  title         VARCHAR(200)  NOT NULL COMMENT '验收标题',
  plan_finish_date DATE       DEFAULT NULL COMMENT '计划完工日期',
  finish_content TEXT         DEFAULT NULL COMMENT '完工内容',
  self_check_result TEXT      DEFAULT NULL COMMENT '自检结果',
  remaining_issues TEXT       DEFAULT NULL COMMENT '遗留问题',
  status        VARCHAR(30)   NOT NULL DEFAULT 'draft' COMMENT 'draft/pending/approved/rejected',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  KEY idx_cf_project (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='完工验收表';

-- P.63 竣工图纸表
CREATE TABLE biz_completion_drawing (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  project_id    INT           NOT NULL COMMENT '项目ID',
  drawing_name  VARCHAR(200)  NOT NULL COMMENT '图纸名称',
  drawing_category VARCHAR(50) NOT NULL COMMENT '图纸分类',
  version_no    VARCHAR(20)   NOT NULL DEFAULT '1.0' COMMENT '版本号',
  attachment_id INT           NOT NULL COMMENT '关联附件ID',
  status        TINYINT       NOT NULL DEFAULT 1 COMMENT '1正常/0归档',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  KEY idx_cd_project (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='竣工图纸表';

-- P.64 劳务结算表
CREATE TABLE biz_labor_settlement (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  settlement_no VARCHAR(20)   NOT NULL COMMENT '结算编号',
  project_id    INT           NOT NULL COMMENT '项目ID',
  contract_id   INT           NOT NULL COMMENT '劳务合同ID',
  settlement_amount DECIMAL(14,2) NOT NULL COMMENT '结算金额',
  paid_amount   DECIMAL(14,2) NOT NULL DEFAULT 0 COMMENT '已付金额',
  apply_pay_amount DECIMAL(14,2) NOT NULL COMMENT '申请付款金额',
  status        VARCHAR(30)   NOT NULL DEFAULT 'draft' COMMENT 'draft/pending/approved/rejected',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  UNIQUE KEY uk_settlement_no (settlement_no),
  KEY idx_ls_project (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='劳务结算表';

-- P.65 案例展示表
CREATE TABLE biz_case (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  project_id    INT           DEFAULT NULL COMMENT '关联项目ID',
  case_name     VARCHAR(200)  NOT NULL COMMENT '案例名称',
  case_type     VARCHAR(50)   DEFAULT NULL COMMENT '案例类型',
  summary       VARCHAR(500)  DEFAULT NULL COMMENT '摘要',
  content       TEXT          DEFAULT NULL COMMENT '详细内容',
  cover_image_id INT          DEFAULT NULL COMMENT '封面图片附件ID',
  display_order INT           NOT NULL DEFAULT 0 COMMENT '展示排序',
  status        VARCHAR(30)   NOT NULL DEFAULT 'draft' COMMENT 'draft/published/offline',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='案例展示表';

-- P.66 报表预计算缓存表
CREATE TABLE biz_report_cache (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  report_type   VARCHAR(50)   NOT NULL COMMENT '报表类型',
  filter_hash   VARCHAR(64)   NOT NULL COMMENT '筛选条件Hash',
  data_json     JSON          NOT NULL COMMENT '预聚合数据JSON',
  calc_time     DATETIME      NOT NULL COMMENT '计算时间',
  expired_at    DATETIME      NOT NULL COMMENT '过期时间',
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_rc_type_hash (report_type, filter_hash),
  KEY idx_rc_expired (expired_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报表预计算缓存表';

-- P.67 报表订阅配置表
CREATE TABLE biz_report_subscribe (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  user_id       INT           NOT NULL COMMENT '订阅用户ID',
  report_type   VARCHAR(50)   NOT NULL COMMENT '报表类型',
  frequency     VARCHAR(20)   NOT NULL COMMENT '推送频率:daily/weekly/monthly',
  push_format   VARCHAR(20)   NOT NULL DEFAULT 'wechat' COMMENT '推送方式:wechat',
  status        TINYINT       NOT NULL DEFAULT 1 COMMENT '1已订阅/0已取消',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  KEY idx_rs_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报表订阅配置表';

-- P.68 审批实例表
CREATE TABLE biz_approval_instance (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  biz_type      VARCHAR(50)   NOT NULL COMMENT '关联业务类型',
  biz_id        INT           NOT NULL COMMENT '关联业务单据ID',
  flow_def_id   INT           NOT NULL COMMENT '关联流程定义ID',
  current_node  INT           NOT NULL DEFAULT 1 COMMENT '当前审批节点序号',
  status        VARCHAR(30)   NOT NULL DEFAULT 'pending' COMMENT 'pending/approved/rejected/cancelled',
  initiator_id  INT           NOT NULL COMMENT '发起人ID',
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_ai_biz (biz_type, biz_id, status),
  KEY idx_ai_initiator (initiator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='审批实例表';

-- P.69 审批记录表
CREATE TABLE biz_approval_record (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  instance_id   INT           NOT NULL COMMENT '关联审批实例ID',
  node_order    INT           NOT NULL COMMENT '节点序号',
  node_name     VARCHAR(100)  NOT NULL COMMENT '节点名称',
  approver_id   INT           NOT NULL COMMENT '审批人ID',
  action        VARCHAR(30)   NOT NULL COMMENT '操作类型:approve/reject/cancel/delegate/read/cc',
  opinion       VARCHAR(500)  DEFAULT NULL COMMENT '审批意见',
  delegate_from_id INT        DEFAULT NULL COMMENT '转办来源用户ID(action=delegate时记录)',
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_ar_instance_node (instance_id, node_order),
  KEY idx_ar_approver (approver_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='审批记录表';

-- P.70 业务附件表
CREATE TABLE biz_attachment (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  file_name     VARCHAR(200)  NOT NULL COMMENT '原始文件名',
  file_path     VARCHAR(500)  NOT NULL COMMENT 'MinIO存储路径',
  file_size     BIGINT        NOT NULL COMMENT '文件大小(字节)',
  file_type     VARCHAR(50)   NOT NULL COMMENT '文件MIME类型',
  file_ext      VARCHAR(20)   NOT NULL COMMENT '文件扩展名',
  md5           VARCHAR(32)   DEFAULT NULL COMMENT '文件MD5(秒传用)',
  biz_type      VARCHAR(50)   NOT NULL COMMENT '业务类型枚举',
  biz_id        INT           NOT NULL COMMENT '关联业务单据ID',
  status        TINYINT       NOT NULL DEFAULT 1 COMMENT '1正常/0已替换',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT       NOT NULL DEFAULT 0,
  KEY idx_att_biz (biz_type, biz_id),
  KEY idx_att_md5 (md5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='业务附件表';

-- P.71 异常工单表
CREATE TABLE biz_exception_task (
  id            INT           PRIMARY KEY AUTO_INCREMENT,
  biz_type      VARCHAR(50)   NOT NULL COMMENT '业务类型',
  biz_id        INT           NOT NULL COMMENT '关联业务单据ID',
  fail_reason   TEXT          NOT NULL COMMENT '失败原因',
  handler_id    INT           DEFAULT NULL COMMENT '处理人ID',
  resolve_remark VARCHAR(500) DEFAULT NULL COMMENT '处理备注',
  status        TINYINT       NOT NULL DEFAULT 1 COMMENT '1待处理/2已处理',
  creator_id    INT           NOT NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_et_status (status),
  KEY idx_et_biz (biz_type, biz_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='异常工单表';

-- P.72 编号种子表
CREATE TABLE biz_no_seed (
  prefix        VARCHAR(10)   NOT NULL COMMENT '编号前缀(V/P/IC/EC等)',
  date_part     VARCHAR(10)   NOT NULL COMMENT '日期部分(YYMMDD或YYMM或global)',
  current_seq   INT           NOT NULL DEFAULT 0 COMMENT '当前序号',
  PRIMARY KEY (prefix, date_part)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='编号种子表';

-- ============================================================
-- 第三部分：初始化数据
-- ============================================================

-- 超级管理员
INSERT INTO sys_user (id, username, password_hash, real_name, phone, dept_id, status, creator_id) VALUES
(1, 'admin', '$2a$10$WFhI3yqqRL4ngA9ZSYnOP.USIo355xFs1rxRPszVAiif9x8WbeQ.6', '系统管理员', '13800000000', 1, 1, 1);

-- 默认部门
INSERT INTO sys_dept (id, name, parent_id, level, path, sort, status, creator_id) VALUES
(1, '总公司', 0, 1, '/1/', 1, 1, 1);

-- 11个角色 (按V3.2规格)
INSERT INTO sys_role (id, role_code, role_name, data_scope, status, creator_id) VALUES
(1,  'GM',          '总经理',     1, 1, 1),
(2,  'PROJ_MGR',    '项目经理',   4, 1, 1),
(3,  'BUDGET',      '预算员',     4, 1, 1),
(4,  'PURCHASE',    '采购员',     4, 1, 1),
(5,  'FINANCE',     '财务人员',   4, 1, 1),
(6,  'LEGAL',       '法务人员',   1, 1, 1),
(7,  'DATA',        '资料员',     4, 1, 1),
(8,  'HR',          'HR管理员',   1, 1, 1),
(9,  'BASE',        '基层员工',   3, 1, 1),
(10, 'SOFT',        '软件管理员', 1, 1, 1),
(11, 'TEAM_MEMBER', '班组成员',   3, 1, 1);

-- 管理员绑定角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 10);
