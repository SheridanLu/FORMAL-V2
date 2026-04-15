package com.mochu.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochu.business.dto.HiddenItemDTO;
import com.mochu.business.entity.BizHiddenItem;
import com.mochu.business.mapper.BizHiddenItemMapper;
import com.mochu.common.exception.BusinessException;
import com.mochu.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 暗项管理 Service
 *
 * <p>V3.0: 暗项发现、报价、审批、结算全流程
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HiddenItemService {

    private final BizHiddenItemMapper hiddenItemMapper;

    public PageResult<BizHiddenItem> list(Integer projectId, Integer contractId,
                                           String status, String itemType,
                                           int page, int size) {
        Page<BizHiddenItem> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<BizHiddenItem> wrapper = new LambdaQueryWrapper<>();
        if (projectId != null) wrapper.eq(BizHiddenItem::getProjectId, projectId);
        if (contractId != null) wrapper.eq(BizHiddenItem::getContractId, contractId);
        if (StringUtils.hasText(status)) wrapper.eq(BizHiddenItem::getStatus, status);
        if (StringUtils.hasText(itemType)) wrapper.eq(BizHiddenItem::getItemType, itemType);
        wrapper.orderByDesc(BizHiddenItem::getCreatedAt);
        hiddenItemMapper.selectPage(pageParam, wrapper);
        return new PageResult<>(pageParam.getRecords(), pageParam.getTotal(), page, size);
    }

    public BizHiddenItem getById(Integer id) {
        BizHiddenItem item = hiddenItemMapper.selectById(id);
        if (item == null) throw new BusinessException("暗项不存在");
        return item;
    }

    public void create(HiddenItemDTO dto) {
        BizHiddenItem entity = new BizHiddenItem();
        BeanUtils.copyProperties(dto, entity);
        // 自动计算总金额
        if (entity.getQuantity() != null && entity.getUnitPrice() != null && entity.getTotalAmount() == null) {
            entity.setTotalAmount(entity.getQuantity().multiply(entity.getUnitPrice()));
        }
        if (entity.getStatus() == null) entity.setStatus("identified");
        hiddenItemMapper.insert(entity);
    }

    public void update(Integer id, HiddenItemDTO dto) {
        BizHiddenItem entity = hiddenItemMapper.selectById(id);
        if (entity == null) throw new BusinessException("暗项不存在");
        BeanUtils.copyProperties(dto, entity, "id");
        // 重新计算总金额
        if (entity.getQuantity() != null && entity.getUnitPrice() != null) {
            entity.setTotalAmount(entity.getQuantity().multiply(entity.getUnitPrice()));
        }
        hiddenItemMapper.updateById(entity);
    }

    public void updateStatus(Integer id, String status) {
        BizHiddenItem entity = hiddenItemMapper.selectById(id);
        if (entity == null) throw new BusinessException("暗项不存在");
        entity.setStatus(status);
        hiddenItemMapper.updateById(entity);
    }

    public void delete(Integer id) {
        hiddenItemMapper.deleteById(id);
    }

    /**
     * 按项目汇总暗项金额
     */
    public Map<String, Object> summary(Integer projectId) {
        LambdaQueryWrapper<BizHiddenItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizHiddenItem::getProjectId, projectId);

        var all = hiddenItemMapper.selectList(wrapper);
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal estimatedCost = BigDecimal.ZERO;
        int count = all.size();

        for (BizHiddenItem item : all) {
            if (item.getTotalAmount() != null) totalAmount = totalAmount.add(item.getTotalAmount());
            if (item.getEstimatedCost() != null) estimatedCost = estimatedCost.add(item.getEstimatedCost());
        }

        return Map.of(
                "count", count,
                "totalAmount", totalAmount,
                "estimatedCost", estimatedCost
        );
    }
}
