-- ====================================================================
-- V3.4 材料管理标准化改造
-- 日期: 2026-04-08
-- 说明: 字段重命名(base_price → base_price_with_tax)、新增税率字段、
--       历史数据清洗、添加唯一索引
-- ====================================================================

-- 1. 字段变更
ALTER TABLE biz_material_base
  CHANGE COLUMN base_price base_price_with_tax DECIMAL(14,2) NOT NULL DEFAULT 0.00 COMMENT '含税基准价';

ALTER TABLE biz_material_base
  ADD COLUMN tax_rate TINYINT NOT NULL DEFAULT 13 COMMENT '税率(0/1/3/6/9/13)' AFTER base_price_with_tax;

ALTER TABLE biz_material_base
  MODIFY COLUMN category VARCHAR(10) NOT NULL DEFAULT '材料' COMMENT '分类(设备/材料/人工)';

ALTER TABLE biz_material_base
  MODIFY COLUMN unit VARCHAR(10) NOT NULL DEFAULT '项' COMMENT '单位(米/吨/卷/箱/台/项/只/套)';

-- 2. 历史数据清洗 — 分类映射
UPDATE biz_material_base SET category = '设备'
WHERE category IN ('设备', '仪器', '仪表', '机械', '机具');

UPDATE biz_material_base SET category = '材料'
WHERE category NOT IN ('设备', '人工') OR category IS NULL OR category = '';

UPDATE biz_material_base SET category = '人工'
WHERE category IN ('人工', '劳务');

-- 3. 历史数据清洗 — 单位映射
UPDATE biz_material_base SET unit = '米' WHERE unit IN ('m', 'M', '米');
UPDATE biz_material_base SET unit = '吨' WHERE unit IN ('t', 'T', '吨');
UPDATE biz_material_base SET unit = '卷' WHERE unit IN ('卷');
UPDATE biz_material_base SET unit = '箱' WHERE unit IN ('箱');
UPDATE biz_material_base SET unit = '台' WHERE unit IN ('台');
UPDATE biz_material_base SET unit = '项' WHERE unit NOT IN ('米','吨','卷','箱','台','项','只','套') OR unit IS NULL OR unit = '';
UPDATE biz_material_base SET unit = '只' WHERE unit IN ('只', '个', '枚');
UPDATE biz_material_base SET unit = '套' WHERE unit IN ('套', '组');

-- 4. 税率默认值
UPDATE biz_material_base SET tax_rate = 13 WHERE tax_rate IS NULL OR tax_rate NOT IN (0, 1, 3, 6, 9, 13);

-- 5. 添加唯一索引（同名+同规格+同单位不可重复）
-- 先处理可能的重复数据（保留id最小的记录）
-- 注意：执行前请先检查是否有重复数据，如有需手工处理
-- SELECT material_name, spec_model, unit, COUNT(*) cnt FROM biz_material_base
-- WHERE deleted = 0 GROUP BY material_name, spec_model, unit HAVING cnt > 1;

ALTER TABLE biz_material_base
  ADD UNIQUE INDEX uk_name_spec_unit (material_name, spec_model, unit);

-- 6. 验证
SELECT '=== V3.4 材料标准化迁移完成 ===' AS info;
SELECT category, COUNT(*) FROM biz_material_base WHERE deleted = 0 GROUP BY category;
SELECT unit, COUNT(*) FROM biz_material_base WHERE deleted = 0 GROUP BY unit;
