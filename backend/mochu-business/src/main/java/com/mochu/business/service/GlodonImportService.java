package com.mochu.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochu.business.entity.BizGlodonImport;
import com.mochu.business.mapper.BizGlodonImportMapper;
import com.mochu.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 广联达数据导入 Service
 *
 * <p>V3.0: 支持解析广联达导出的 Excel 文件（清单计价/概算汇总格式）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GlodonImportService {

    private final BizGlodonImportMapper importMapper;

    public PageResult<BizGlodonImport> list(Integer projectId, int page, int size) {
        Page<BizGlodonImport> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<BizGlodonImport> wrapper = new LambdaQueryWrapper<>();
        if (projectId != null) wrapper.eq(BizGlodonImport::getProjectId, projectId);
        wrapper.orderByDesc(BizGlodonImport::getCreatedAt);
        importMapper.selectPage(pageParam, wrapper);
        return new PageResult<>(pageParam.getRecords(), pageParam.getTotal(), page, size);
    }

    /**
     * 解析并导入广联达 Excel 文件
     */
    public BizGlodonImport importExcel(Integer projectId, String importType, MultipartFile file) {
        BizGlodonImport record = new BizGlodonImport();
        record.setProjectId(projectId);
        record.setFileName(file.getOriginalFilename());
        record.setImportType(importType);
        record.setStatus("processing");
        importMapper.insert(record);

        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getLastRowNum();
            record.setTotalRows(totalRows);

            // 解析表头
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                record.setStatus("failed");
                record.setErrorMsg("文件为空或格式不正确");
                importMapper.updateById(record);
                return record;
            }

            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(getCellStringValue(cell));
            }

            // 解析数据行
            int successRows = 0;
            List<Map<String, String>> dataRows = new ArrayList<>();
            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Map<String, String> rowData = new LinkedHashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j);
                    rowData.put(headers.get(j), getCellStringValue(cell));
                }
                dataRows.add(rowData);
                successRows++;
            }

            // TODO: 根据 importType 将解析的数据写入对应业务表
            // cost -> biz_cost_ledger
            // quantity -> biz_contract_material (工程量)
            // price -> biz_material_price (材料价格)
            log.info("广联达导入: 解析{}行数据, 表头: {}", successRows, headers);

            record.setSuccessRows(successRows);
            record.setStatus("success");
            importMapper.updateById(record);
            workbook.close();
        } catch (Exception e) {
            log.error("广联达导入失败", e);
            record.setStatus("failed");
            record.setErrorMsg(e.getMessage());
            importMapper.updateById(record);
        }
        return record;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toString();
                }
                yield String.valueOf(cell.getNumericCellValue());
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield String.valueOf(cell.getNumericCellValue());
                } catch (Exception e) {
                    yield cell.getStringCellValue();
                }
            }
            default -> "";
        };
    }

    public void deleteImport(Integer id) {
        importMapper.deleteById(id);
    }
}
