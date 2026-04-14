package com.mochu.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mochu.business.dto.ContractPaymentPlanDTO;
import com.mochu.business.entity.BizContract;
import com.mochu.business.entity.BizContractPaymentPlan;
import com.mochu.business.mapper.BizContractMapper;
import com.mochu.business.mapper.BizContractPaymentPlanMapper;
import com.mochu.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractPaymentPlanService {

    private final BizContractPaymentPlanMapper planMapper;
    private final BizContractMapper contractMapper;

    /**
     * 创建付款计划
     */
    @Transactional
    public void create(Integer contractId, List<ContractPaymentPlanDTO> plans,
                       Integer creatorId) {
        BizContract contract = contractMapper.selectById(contractId);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }

        // 校验计划金额之和不超过合同含税金额
        BigDecimal totalPlan = plans.stream()
                .map(ContractPaymentPlanDTO::getPlanAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPlan.compareTo(contract.getAmountWithTax()) > 0) {
            throw new BusinessException(20001,
                    "付款计划总金额不能超过合同含税金额");
        }

        // 删除已有计划（重新生成）
        planMapper.delete(new LambdaQueryWrapper<BizContractPaymentPlan>()
                .eq(BizContractPaymentPlan::getContractId, contractId));

        // 批量插入
        for (int i = 0; i < plans.size(); i++) {
            ContractPaymentPlanDTO dto = plans.get(i);
            BizContractPaymentPlan entity = new BizContractPaymentPlan();
            BeanUtils.copyProperties(dto, entity);
            entity.setContractId(contractId);
            entity.setPlanNo(i + 1);
            entity.setStatus("pending");
            entity.setCreatorId(creatorId);
            planMapper.insert(entity);
        }
    }

    /**
     * 查询合同的付款计划
     */
    public List<BizContractPaymentPlan> listByContractId(Integer contractId) {
        return planMapper.selectList(
                new LambdaQueryWrapper<BizContractPaymentPlan>()
                        .eq(BizContractPaymentPlan::getContractId, contractId)
                        .eq(BizContractPaymentPlan::getDeleted, 0)
                        .orderByAsc(BizContractPaymentPlan::getPlanNo));
    }
}
