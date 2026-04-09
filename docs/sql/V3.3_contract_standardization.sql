-- ============================================================
-- MOCHU-OA V3.3 — 合同标准化整改增量迁移
-- 执行方式: docker exec -i mochu-mysql mysql -uroot -p'mochu@2026' mochu_oa < docs/sql/V3.3_contract_standardization.sql
-- ============================================================

USE mochu_oa;

-- ============================================================
-- 1. 合同模板主表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_contract_tpl (
  id              INT           PRIMARY KEY AUTO_INCREMENT,
  contract_type   VARCHAR(30)   NOT NULL COMMENT '绑定的合同类型枚举',
  tpl_name        VARCHAR(200)  NOT NULL COMMENT '模板名称',
  description     VARCHAR(500)  DEFAULT NULL COMMENT '模板说明',
  status          TINYINT       NOT NULL DEFAULT 1 COMMENT '1启用/0停用',
  creator_id      INT           NOT NULL,
  created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted         TINYINT       NOT NULL DEFAULT 0,
  UNIQUE KEY uk_tpl_type (contract_type, deleted),
  KEY idx_tpl_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='合同模板主表';

-- ============================================================
-- 2. 模板版本表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_contract_tpl_version (
  id              INT           PRIMARY KEY AUTO_INCREMENT,
  tpl_id          INT           NOT NULL COMMENT '模板主表ID',
  version_no      INT           NOT NULL DEFAULT 1 COMMENT '版本号(递增)',
  file_path       VARCHAR(500)  NOT NULL COMMENT 'MinIO存储路径',
  file_name       VARCHAR(200)  NOT NULL COMMENT '原始文件名',
  file_md5        VARCHAR(64)   NOT NULL COMMENT '文件MD5(防篡改)',
  html_cache      LONGTEXT      DEFAULT NULL COMMENT '解析后的HTML缓存(含占位符)',
  status          TINYINT       NOT NULL DEFAULT 1 COMMENT '1启用/0停用',
  effective_from  DATETIME      DEFAULT NULL COMMENT '生效开始时间',
  effective_until DATETIME      DEFAULT NULL COMMENT '生效截止时间',
  creator_id      INT           NOT NULL,
  created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_tpl_version (tpl_id, version_no),
  KEY idx_tpl_version_status (tpl_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='合同模板版本表';

-- ============================================================
-- 3. 模板字段定义表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_contract_tpl_field (
  id              INT           PRIMARY KEY AUTO_INCREMENT,
  version_id      INT           NOT NULL COMMENT '模板版本ID',
  field_key       VARCHAR(50)   NOT NULL COMMENT '字段标识(占位符名)',
  field_name      VARCHAR(100)  NOT NULL COMMENT '字段中文名',
  field_type      VARCHAR(20)   NOT NULL DEFAULT 'text' COMMENT 'text/number/date/select/textarea',
  required        TINYINT       NOT NULL DEFAULT 0 COMMENT '是否必填',
  options_json    VARCHAR(1000) DEFAULT NULL COMMENT 'select选项JSON',
  default_value   VARCHAR(500)  DEFAULT NULL,
  sort_order      INT           NOT NULL DEFAULT 0,
  placeholder     VARCHAR(200)  DEFAULT NULL,
  max_length      INT           DEFAULT NULL,
  validation_rule VARCHAR(200)  DEFAULT NULL COMMENT '正则校验规则',
  KEY idx_field_version (version_id, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='合同模板字段定义表';

-- ============================================================
-- 4. 合同字段值表(实例级)
-- ============================================================
CREATE TABLE IF NOT EXISTS biz_contract_field_value (
  id              INT           PRIMARY KEY AUTO_INCREMENT,
  contract_id     INT           NOT NULL COMMENT '合同ID',
  field_key       VARCHAR(50)   NOT NULL COMMENT '字段标识',
  field_value     TEXT          DEFAULT NULL COMMENT '填写值',
  created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_contract_field (contract_id, field_key),
  KEY idx_field_contract (contract_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='合同字段值表';

-- ============================================================
-- 5. 模板操作审计日志
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_contract_tpl_audit (
  id              INT           PRIMARY KEY AUTO_INCREMENT,
  tpl_id          INT           NOT NULL COMMENT '模板ID',
  version_id      INT           DEFAULT NULL COMMENT '版本ID',
  action          VARCHAR(30)   NOT NULL COMMENT 'upload/enable/disable/replace/delete',
  detail          VARCHAR(500)  DEFAULT NULL COMMENT '操作详情',
  operator_id     INT           NOT NULL,
  operated_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_audit_tpl (tpl_id),
  KEY idx_audit_time (operated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='合同模板审计日志表';

-- ============================================================
-- 6. 扩展 biz_contract 表: 新增 tpl_version_id 列
-- ============================================================
ALTER TABLE biz_contract
  ADD COLUMN tpl_version_id INT DEFAULT NULL COMMENT '签订时的模板版本ID' AFTER template_id;
