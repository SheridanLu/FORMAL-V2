package com.mochu.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private static final Set<String> VALID_STATUSES = Set.of("identified", "quoted", "approved", "settled");

    public void updateStatus(Integer id, String status) {
        if (!VALID_STATUSES.contains(status)) {
            throw new IllegalArgumentException("非法状态值");
        }
        BizHiddenItem entity = hiddenItemMapper.selectById(id);
        if (entity == null) throw new BusinessException("暗项不存在");
        entity.setStatus(status);
        hiddenItemMapper.updateById(entity);
    }

    public void delete(Integer id) {
        hiddenItemMapper.deleteById(id);
    }

    /**
     * 按项目汇总暗项金额（SQL 聚合，避免全表加载）
     */
    public Map<String, Object> summary(Integer projectId) {
        QueryWrapper<BizHiddenItem> qw = new QueryWrapper<>();
        qw.select("COUNT(*) AS total_count",
                  "IFNULL(SUM(total_amount),0) AS total_amount",
                  "IFNULL(SUM(estimated_cost),0) AS estimated_cost")
          .eq("project_id", projectId)
          .eq("deleted", 0);
        List<Map<String, Object>> result = hiddenItemMapper.selectMaps(qw);
        return result.isEmpty() ? Map.of("total_count", 0, "total_amount", 0, "estimated_cost", 0) : result.get(0);
    }
}
