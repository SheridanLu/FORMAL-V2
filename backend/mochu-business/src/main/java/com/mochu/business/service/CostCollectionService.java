package com.mochu.business.service;

import com.mochu.business.entity.*;
import com.mochu.business.mapper.*;
import com.mochu.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CostCollectionService {

    private final BizCostLedgerMapper costLedgerMapper;
    private final BizPaymentApplyMapper paymentMapper;
    private final BizContractMapper contractMapper;
    private final BizExceptionTaskMapper exceptionTaskMapper;

    /**
     * 合同类型 → 成本科目映射
     * key: contract_type, value: [一级科目, 二级科目]
     */
    private static final Map<String, String[]> COST_CATEGORY_MAP = Map.of(
            "equipment_purchase", new String[]{"设备费", "设备采购费"},
            "equipment_rental",   new String[]{"设备费", "租赁费"},
            "material",           new String[]{"材料费", "主材费"},
            "auxiliary_material", new String[]{"材料费", "辅材费"},
            "labor",              new String[]{"人工费", "劳务工资"},
            "subcontract",        new String[]{"分包费", "专业分包费"},
            "software",           new String[]{"软件费", "软件采购费"},
            "spot_purchase",      new String[]{"其他费", "零星采购费"}
    );

    /**
     * 付款确认后触发成本归集
     */
    @Transactional
    public void collectCost(Integer paymentId, Integer operatorId) {
        BizPaymentApply payment = paymentMapper.selectById(paymentId);
        if (payment == null) {
            throw new BusinessException("付款申请不存在");
        }

        try {
            // 查询关联合同
            BizContract contract = contractMapper.selectById(payment.getContractId());
            String contractType = contract != null ? contract.getContractType() : "other";

            // 映射科目
            String[] categories = COST_CATEGORY_MAP.getOrDefault(
                    contractType, new String[]{"其他费", "其他"});

            // 写入成本台账
            BizCostLedger ledger = new BizCostLedger();
            ledger.setProjectId(payment.getProjectId());
            ledger.setBizType("payment");
            ledger.setBizId(paymentId);
            ledger.setCategoryL1(categories[0]);
            ledger.setCategoryL2(categories[1]);
            ledger.setAmount(payment.getAmount());
            ledger.setOccurDate(payment.getCreatedAt().toLocalDate());
            ledger.setRemark("付款确认自动归集");
            ledger.setCreatorId(operatorId);
            costLedgerMapper.insert(ledger);

            // 更新付款状态 → collected
            payment.setStatus("collected");
            paymentMapper.updateById(payment);

            log.info("成本归集成功: paymentId={}, amount={}, category={}/{}",
                    paymentId, payment.getAmount(), categories[0], categories[1]);

        } catch (Exception e) {
            log.error("成本归集失败: paymentId={}", paymentId, e);
            // 状态 → collect_failed
            payment.setStatus("collect_failed");
            paymentMapper.updateById(payment);

            // 创建异常工单
            createExceptionTask(payment, e.getMessage());
        }
    }

    /**
     * 归集失败 → 创建异常工单
     */
    private void createExceptionTask(BizPaymentApply payment, String errorMsg) {
        BizExceptionTask task = new BizExceptionTask();
        task.setProjectId(payment.getProjectId());
        task.setBizType("payment");
        task.setBizId(payment.getId());
        task.setTitle("成本归集失败 - 付款编号: " + payment.getPaymentNo());
        task.setDescription("归集异常: " + errorMsg);
        task.setStatus(0); // 待处理
        exceptionTaskMapper.insert(task);

        log.warn("异常工单已创建: paymentId={}, error={}", payment.getId(), errorMsg);
    }
}
