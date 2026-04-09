package com.mochu.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochu.business.dto.MaterialBatchDTO;
import com.mochu.business.dto.MaterialDTO;
import com.mochu.business.entity.BizMaterialBase;
import com.mochu.business.enums.MaterialCategoryEnum;
import com.mochu.business.enums.MaterialTaxRateEnum;
import com.mochu.business.enums.MaterialUnitEnum;
import com.mochu.business.mapper.BizMaterialBaseMapper;
import com.mochu.business.vo.BatchResult;
import com.mochu.common.constant.Constants;
import com.mochu.common.exception.BusinessException;
import com.mochu.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialService {

    private final BizMaterialBaseMapper materialMapper;
    private final NoGeneratorService noGeneratorService;

    public PageResult<BizMaterialBase> list(String materialName, String category, String status, Integer page, Integer size) {
        if (page == null || page < 1) page = Constants.DEFAULT_PAGE;
        if (size == null || size < 1) size = Constants.DEFAULT_SIZE;

        Page<BizMaterialBase> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<BizMaterialBase> wrapper = new LambdaQueryWrapper<>();

        if (materialName != null && !materialName.isBlank()) {
            wrapper.like(BizMaterialBase::getMaterialName, materialName);
        }
        if (category != null && !category.isBlank()) {
            wrapper.eq(BizMaterialBase::getCategory, category);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(BizMaterialBase::getStatus, status);
        }
        wrapper.orderByDesc(BizMaterialBase::getCreatedAt);

        materialMapper.selectPage(pageParam, wrapper);
        return new PageResult<>(pageParam.getRecords(), pageParam.getTotal(), page, size);
    }

    public BizMaterialBase getById(Integer id) {
        return materialMapper.selectById(id);
    }

    /**
     * 批量创建材料 — 枚举校验 + 行内去重 + 数据库去重 + 事务批量插入
     */
    @Transactional
    public BatchResult batchCreate(MaterialBatchDTO dto) {
        List<MaterialBatchDTO.MaterialBatchItem> items = dto.getItems();
        List<BatchResult.BatchError> errors = new ArrayList<>();
        Set<Integer> errorRows = new HashSet<>();

        // 1. 逐行枚举校验
        for (int i = 0; i < items.size(); i++) {
            int row = i + 1;
            MaterialBatchDTO.MaterialBatchItem item = items.get(i);

            if (!MaterialCategoryEnum.isValid(item.getCategory())) {
                errors.add(new BatchResult.BatchError(row, "category", "无效的材料分类，必须为：设备、材料、人工"));
                errorRows.add(i);
            }
            if (!MaterialUnitEnum.isValid(item.getUnit())) {
                errors.add(new BatchResult.BatchError(row, "unit", "无效的计量单位"));
                errorRows.add(i);
            }
            if (!MaterialTaxRateEnum.isValid(item.getTaxRate())) {
                errors.add(new BatchResult.BatchError(row, "tax_rate", "无效的税率，必须为：0、1、3、6、9、13"));
                errorRows.add(i);
            }
        }

        // 2. 行内去重：name+spec+unit 三元组
        Map<String, Integer> seen = new HashMap<>();
        for (int i = 0; i < items.size(); i++) {
            if (errorRows.contains(i)) continue;
            MaterialBatchDTO.MaterialBatchItem item = items.get(i);
            String key = buildDedupKey(item.getMaterialName(), item.getSpecModel(), item.getUnit());
            if (seen.containsKey(key)) {
                int row = i + 1;
                errors.add(new BatchResult.BatchError(row, "material_name", "与第" + seen.get(key) + "行重复(同名+同规格+同单位)"));
                errorRows.add(i);
            } else {
                seen.put(key, i + 1);
            }
        }

        // 3. 数据库去重
        List<MaterialBatchDTO.MaterialBatchItem> validItems = new ArrayList<>();
        List<Integer> validOriginalIndexes = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (!errorRows.contains(i)) {
                validItems.add(items.get(i));
                validOriginalIndexes.add(i);
            }
        }

        if (!validItems.isEmpty()) {
            // 查询数据库中已存在的 name+spec+unit 组合
            Set<String> existingKeys = new HashSet<>();
            // 分批查询避免 SQL 过长
            for (MaterialBatchDTO.MaterialBatchItem item : validItems) {
                LambdaQueryWrapper<BizMaterialBase> qw = new LambdaQueryWrapper<>();
                qw.eq(BizMaterialBase::getMaterialName, item.getMaterialName())
                  .eq(BizMaterialBase::getUnit, item.getUnit());
                if (item.getSpecModel() != null && !item.getSpecModel().isBlank()) {
                    qw.eq(BizMaterialBase::getSpecModel, item.getSpecModel());
                } else {
                    qw.and(w -> w.isNull(BizMaterialBase::getSpecModel).or().eq(BizMaterialBase::getSpecModel, ""));
                }
                Long count = materialMapper.selectCount(qw);
                if (count > 0) {
                    existingKeys.add(buildDedupKey(item.getMaterialName(), item.getSpecModel(), item.getUnit()));
                }
            }

            // 标记数据库重复行
            Iterator<MaterialBatchDTO.MaterialBatchItem> iter = validItems.iterator();
            int idx = 0;
            while (iter.hasNext()) {
                MaterialBatchDTO.MaterialBatchItem item = iter.next();
                String key = buildDedupKey(item.getMaterialName(), item.getSpecModel(), item.getUnit());
                if (existingKeys.contains(key)) {
                    int originalIndex = validOriginalIndexes.get(idx);
                    int row = originalIndex + 1;
                    errors.add(new BatchResult.BatchError(row, "material_name", "同名+同规格+同单位的材料已存在"));
                    iter.remove();
                    validOriginalIndexes.remove(idx);
                } else {
                    idx++;
                }
            }
        }

        // 4. 全部校验失败
        if (validItems.isEmpty()) {
            return new BatchResult(0, items.size(), errors);
        }

        // 5. 批量插入通过校验的行
        for (MaterialBatchDTO.MaterialBatchItem item : validItems) {
            BizMaterialBase entity = new BizMaterialBase();
            entity.setMaterialName(item.getMaterialName());
            entity.setSpecModel(item.getSpecModel());
            entity.setCategory(item.getCategory());
            entity.setUnit(item.getUnit());
            entity.setBasePriceWithTax(item.getBasePriceWithTax());
            entity.setTaxRate(item.getTaxRate());
            entity.setMaterialCode(noGeneratorService.generate("M", 6));
            entity.setStatus("active");
            materialMapper.insert(entity);
        }

        int successCount = validItems.size();
        int failCount = items.size() - successCount;
        return new BatchResult(successCount, failCount, errors);
    }

    /**
     * 单条更新 — 枚举校验
     */
    public void update(Integer id, MaterialDTO dto) {
        BizMaterialBase entity = materialMapper.selectById(id);
        if (entity == null) throw new BusinessException("材料不存在");

        // 枚举校验
        if (!MaterialCategoryEnum.isValid(dto.getCategory())) {
            throw new BusinessException("无效的材料分类，必须为：设备、材料、人工");
        }
        if (!MaterialUnitEnum.isValid(dto.getUnit())) {
            throw new BusinessException("无效的计量单位");
        }
        if (!MaterialTaxRateEnum.isValid(dto.getTaxRate())) {
            throw new BusinessException("无效的税率，必须为：0、1、3、6、9、13");
        }

        // 去重校验（排除自身）
        LambdaQueryWrapper<BizMaterialBase> qw = new LambdaQueryWrapper<>();
        qw.eq(BizMaterialBase::getMaterialName, dto.getMaterialName())
          .eq(BizMaterialBase::getUnit, dto.getUnit())
          .ne(BizMaterialBase::getId, id);
        if (dto.getSpecModel() != null && !dto.getSpecModel().isBlank()) {
            qw.eq(BizMaterialBase::getSpecModel, dto.getSpecModel());
        } else {
            qw.and(w -> w.isNull(BizMaterialBase::getSpecModel).or().eq(BizMaterialBase::getSpecModel, ""));
        }
        if (materialMapper.selectCount(qw) > 0) {
            throw new BusinessException("同名+同规格+同单位的材料已存在");
        }

        BeanUtils.copyProperties(dto, entity, "id", "materialCode", "status");
        materialMapper.updateById(entity);
    }

    public void delete(Integer id) {
        materialMapper.deleteById(id);
    }

    public List<BizMaterialBase> listAll() {
        return materialMapper.selectList(
                new LambdaQueryWrapper<BizMaterialBase>()
                        .select(BizMaterialBase::getId, BizMaterialBase::getMaterialCode,
                                BizMaterialBase::getMaterialName, BizMaterialBase::getUnit,
                                BizMaterialBase::getBasePriceWithTax, BizMaterialBase::getTaxRate)
                        .eq(BizMaterialBase::getStatus, "active")
                        .orderByAsc(BizMaterialBase::getMaterialCode));
    }

    private String buildDedupKey(String name, String spec, String unit) {
        return (name != null ? name : "") + "|" + (spec != null ? spec : "") + "|" + (unit != null ? unit : "");
    }
}
