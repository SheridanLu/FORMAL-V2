package com.mochu.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochu.business.dto.CollectionFollowUpDTO;
import com.mochu.business.dto.ReceiptPlanDTO;
import com.mochu.business.entity.BizCollectionFollowUp;
import com.mochu.business.entity.BizReceiptPlan;
import com.mochu.business.mapper.BizCollectionFollowUpMapper;
import com.mochu.business.mapper.BizReceiptPlanMapper;
import com.mochu.common.exception.BusinessException;
import com.mochu.common.result.PageResult;
import com.mochu.common.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

/**
 * 回款督办 Service
 *
 * <p>V3.0: 收款计划管理 + 逾期检测 + 跟进记录
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CollectionSupervisionService {

    private final BizReceiptPlanMapper receiptPlanMapper;
    private final BizCollectionFollowUpMapper followUpMapper;

    // ==================== 收款计划 ====================

    public PageResult<BizReceiptPlan> listPlans(Integer contractId, Integer projectId,
                                                 String status, int page, int size) {
        Page<BizReceiptPlan> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<BizReceiptPlan> wrapper = new LambdaQueryWrapper<>();
        if (contractId != null) wrapper.eq(BizReceiptPlan::getContractId, contractId);
        if (projectId != null) wrapper.eq(BizReceiptPlan::getProjectId, projectId);
        if (StringUtils.hasText(status)) wrapper.eq(BizReceiptPlan::getStatus, status);
        wrapper.orderByAsc(BizReceiptPlan::getPlanDate);
        receiptPlanMapper.selectPage(pageParam, wrapper);
        return new PageResult<>(pageParam.getRecords(), pageParam.getTotal(), page, size);
    }

    public void createPlan(ReceiptPlanDTO dto) {
        BizReceiptPlan entity = new BizReceiptPlan();
        BeanUtils.copyProperties(dto, entity);
        if (entity.getStatus() == null) entity.setStatus("pending");
        receiptPlanMapper.insert(entity);
    }

    public void updatePlan(Integer id, ReceiptPlanDTO dto) {
        BizReceiptPlan entity = receiptPlanMapper.selectById(id);
        if (entity == null) throw new BusinessException("收款计划不存在");
        BeanUtils.copyProperties(dto, entity, "id");
        receiptPlanMapper.updateById(entity);
    }

    /**
     * 确认回款（部分或全部）
     */
    public void confirmReceipt(Integer id, java.math.BigDecimal actualAmount, LocalDate actualDate) {
        BizReceiptPlan entity = receiptPlanMapper.selectById(id);
        if (entity == null) throw new BusinessException("收款计划不存在");
        entity.setActualAmount(actualAmount);
        entity.setActualDate(actualDate != null ? actualDate : LocalDate.now());
        if (actualAmount.compareTo(entity.getPlanAmount()) >= 0) {
            entity.setStatus("completed");
        } else {
            entity.setStatus("partial");
        }
        receiptPlanMapper.updateById(entity);
    }

    public void deletePlan(Integer id) {
        receiptPlanMapper.deleteById(id);
    }

    // ==================== 跟进记录 ====================

    public List<BizCollectionFollowUp> listFollowUps(Integer receiptPlanId) {
        return followUpMapper.selectList(
                new LambdaQueryWrapper<BizCollectionFollowUp>()
                        .eq(BizCollectionFollowUp::getReceiptPlanId, receiptPlanId)
                        .orderByDesc(BizCollectionFollowUp::getFollowUpDate));
    }

    public void createFollowUp(CollectionFollowUpDTO dto) {
        BizCollectionFollowUp entity = new BizCollectionFollowUp();
        BeanUtils.copyProperties(dto, entity);
        entity.setHandlerId(SecurityUtils.getCurrentUserId());
        followUpMapper.insert(entity);
    }

    // ==================== 逾期检测 ====================

    /**
     * V3.0: 每日凌晨检测逾期收款计划
     */
    @Scheduled(cron = "0 30 0 * * ?")
    public void detectOverdue() {
        int count = receiptPlanMapper.update(null, new LambdaUpdateWrapper<BizReceiptPlan>()
                .eq(BizReceiptPlan::getStatus, "pending")
                .lt(BizReceiptPlan::getPlanDate, LocalDate.now())
                .set(BizReceiptPlan::getStatus, "overdue"));
        if (count > 0) {
            log.info("回款督办: 检测到 {} 条逾期收款计划", count);
        }
    }

    /**
     * 逾期统计摘要
     */
    public long countOverdue() {
        return receiptPlanMapper.selectCount(
                new LambdaQueryWrapper<BizReceiptPlan>()
                        .eq(BizReceiptPlan::getStatus, "overdue"));
    }
}
