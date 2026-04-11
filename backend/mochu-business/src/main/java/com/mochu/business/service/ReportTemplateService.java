package com.mochu.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mochu.business.dto.ReportTemplateDTO;
import com.mochu.business.entity.SysReportTemplate;
import com.mochu.business.mapper.SysReportTemplateMapper;
import com.mochu.common.exception.BusinessException;
import com.mochu.common.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 报表引擎服务 — 模板管理 + 动态参数化 SQL 执行
 */
@Service
@RequiredArgsConstructor
public class ReportTemplateService {

    private final SysReportTemplateMapper templateMapper;
    private final JdbcTemplate jdbcTemplate;

    private static final Pattern PARAM_PATTERN = Pattern.compile(":([a-zA-Z_][a-zA-Z0-9_]*)");

    // ==================== 模板 CRUD ====================

    public List<SysReportTemplate> listTemplates(String category) {
        LambdaQueryWrapper<SysReportTemplate> wrapper = new LambdaQueryWrapper<SysReportTemplate>()
                .eq(SysReportTemplate::getStatus, 1)
                .orderByAsc(SysReportTemplate::getCategory)
                .orderByAsc(SysReportTemplate::getId);
        if (category != null && !category.isBlank()) {
            wrapper.eq(SysReportTemplate::getCategory, category);
        }
        return templateMapper.selectList(wrapper);
    }

    public SysReportTemplate getTemplate(Integer id) {
        SysReportTemplate tpl = templateMapper.selectById(id);
        if (tpl == null) throw new BusinessException("报表模板不存在");
        return tpl;
    }

    public void createTemplate(ReportTemplateDTO dto) {
        validateSql(dto.getSqlText());
        SysReportTemplate tpl = new SysReportTemplate();
        tpl.setReportName(dto.getReportName());
        tpl.setCategory(dto.getCategory() != null ? dto.getCategory() : "custom");
        tpl.setChartType(dto.getChartType() != null ? dto.getChartType() : "table");
        tpl.setSqlText(dto.getSqlText());
        tpl.setParamsJson(dto.getParamsJson());
        tpl.setXField(dto.getXField() != null ? dto.getXField() : "");
        tpl.setYFields(dto.getYFields() != null ? dto.getYFields() : "");
        tpl.setDescription(dto.getDescription());
        tpl.setStatus(1);
        tpl.setCreatorId(SecurityUtils.getCurrentUserId());
        templateMapper.insert(tpl);
    }

    public void updateTemplate(Integer id, ReportTemplateDTO dto) {
        SysReportTemplate tpl = templateMapper.selectById(id);
        if (tpl == null) throw new BusinessException("报表模板不存在");
        if (dto.getSqlText() != null) {
            validateSql(dto.getSqlText());
            tpl.setSqlText(dto.getSqlText());
        }
        if (dto.getReportName() != null) tpl.setReportName(dto.getReportName());
        if (dto.getCategory() != null) tpl.setCategory(dto.getCategory());
        if (dto.getChartType() != null) tpl.setChartType(dto.getChartType());
        if (dto.getParamsJson() != null) tpl.setParamsJson(dto.getParamsJson());
        if (dto.getXField() != null) tpl.setXField(dto.getXField());
        if (dto.getYFields() != null) tpl.setYFields(dto.getYFields());
        if (dto.getDescription() != null) tpl.setDescription(dto.getDescription());
        templateMapper.updateById(tpl);
    }

    public void deleteTemplate(Integer id) {
        templateMapper.deleteById(id);
    }

    // ==================== 执行报表 ====================

    /**
     * 执行报表模板，返回数据行列表
     * params: 前端传入参数 Map，key = :paramName 中的 paramName
     */
    public Map<String, Object> executeTemplate(Integer id, Map<String, Object> params) {
        SysReportTemplate tpl = getTemplate(id);
        String sql = tpl.getSqlText();

        // 将 :paramName 替换为 ? 并按顺序收集参数值
        List<Object> argList = new ArrayList<>();
        Matcher matcher = PARAM_PATTERN.matcher(sql);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String paramName = matcher.group(1);
            Object val = params != null ? params.get(paramName) : null;
            argList.add(val != null ? val : "");
            matcher.appendReplacement(sb, "?");
        }
        matcher.appendTail(sb);
        String finalSql = sb.toString();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(finalSql, argList.toArray());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("templateId", tpl.getId());
        result.put("reportName", tpl.getReportName());
        result.put("chartType", tpl.getChartType());
        result.put("xField", tpl.getXField());
        result.put("yFields", tpl.getYFields() != null ? Arrays.asList(tpl.getYFields().split(",")) : List.of());
        result.put("rows", rows);
        result.put("total", rows.size());
        return result;
    }

    // ==================== 内置报表 ====================

    /**
     * 进销存汇总报表（按物料统计入/出/当前库存）
     */
    public List<Map<String, Object>> stockFlowReport(Integer projectId) {
        String sql = """
                SELECT
                    inv.project_id,
                    inv.material_id,
                    inv.material_name,
                    inv.unit,
                    COALESCE(SUM(io.quantity), 0)   AS inbound_qty,
                    COALESCE(SUM(oo.quantity), 0)   AS outbound_qty,
                    inv.current_quantity             AS stock_qty,
                    inv.avg_price,
                    inv.total_amount
                FROM biz_inventory inv
                LEFT JOIN biz_inbound_order io
                    ON io.project_id = inv.project_id AND io.material_id = inv.material_id AND io.deleted = 0
                LEFT JOIN biz_outbound_order oo
                    ON oo.project_id = inv.project_id AND oo.material_id = inv.material_id AND oo.deleted = 0
                WHERE inv.deleted = 0
                """ + (projectId != null ? " AND inv.project_id = " + projectId : "") +
                " GROUP BY inv.project_id, inv.material_id, inv.material_name, inv.unit, inv.current_quantity, inv.avg_price, inv.total_amount";
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * 库龄分析报表（按入库时间分层）
     */
    public List<Map<String, Object>> stockAgingReport(Integer projectId) {
        String sql = """
                SELECT
                    inv.project_id,
                    inv.material_id,
                    inv.material_name,
                    inv.current_quantity,
                    inv.unit,
                    DATEDIFF(NOW(), (
                        SELECT MIN(io2.created_at)
                        FROM biz_inbound_order io2
                        WHERE io2.project_id = inv.project_id
                          AND io2.material_id = inv.material_id
                          AND io2.deleted = 0
                    )) AS age_days,
                    CASE
                        WHEN DATEDIFF(NOW(), (SELECT MIN(io3.created_at) FROM biz_inbound_order io3
                            WHERE io3.project_id=inv.project_id AND io3.material_id=inv.material_id AND io3.deleted=0)) <= 30
                            THEN '30天以内'
                        WHEN DATEDIFF(NOW(), (SELECT MIN(io3.created_at) FROM biz_inbound_order io3
                            WHERE io3.project_id=inv.project_id AND io3.material_id=inv.material_id AND io3.deleted=0)) <= 90
                            THEN '30-90天'
                        WHEN DATEDIFF(NOW(), (SELECT MIN(io3.created_at) FROM biz_inbound_order io3
                            WHERE io3.project_id=inv.project_id AND io3.material_id=inv.material_id AND io3.deleted=0)) <= 180
                            THEN '90-180天'
                        ELSE '180天以上'
                    END AS age_range
                FROM biz_inventory inv
                WHERE inv.deleted = 0 AND inv.current_quantity > 0
                """ + (projectId != null ? " AND inv.project_id = " + projectId : "");
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * 采购价格对比分析
     */
    public List<Map<String, Object>> purchasePriceComparison(Integer materialId) {
        String sql = """
                SELECT
                    item.material_id,
                    item.material_name,
                    item.unit,
                    s.supplier_name,
                    item.unit_price_with_tax AS unit_price,
                    pl.created_at           AS purchase_date,
                    pl.project_id
                FROM biz_purchase_list_item item
                JOIN biz_purchase_list pl ON pl.id = item.list_id AND pl.deleted = 0
                JOIN biz_supplier s ON s.id = item.supplier_id AND s.deleted = 0
                WHERE item.deleted = 0
                """ + (materialId != null ? " AND item.material_id = " + materialId : "") +
                " ORDER BY item.material_id, pl.created_at DESC";
        return jdbcTemplate.queryForList(sql);
    }

    // ==================== 内部校验 ====================

    private void validateSql(String sql) {
        if (sql == null || sql.isBlank()) throw new BusinessException("SQL不能为空");
        String upper = sql.trim().toUpperCase();
        // 只允许 SELECT
        if (!upper.startsWith("SELECT")) throw new BusinessException("只允许 SELECT 查询语句");
        // 禁止危险关键词
        for (String keyword : List.of("DROP", "DELETE", "UPDATE", "INSERT", "TRUNCATE", "ALTER", "CREATE")) {
            if (upper.contains(keyword)) throw new BusinessException("SQL 中包含禁止的操作: " + keyword);
        }
    }
}
