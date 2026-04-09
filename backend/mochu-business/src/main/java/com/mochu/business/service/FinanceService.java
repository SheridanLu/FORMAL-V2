package com.mochu.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochu.business.dto.InvoiceDTO;
import com.mochu.business.dto.PaymentApplyDTO;
import com.mochu.business.dto.ReceiptDTO;
import com.mochu.business.dto.ReimburseDTO;
import com.mochu.business.dto.StatementDTO;
import com.mochu.business.entity.*;
import com.mochu.business.mapper.*;
import com.mochu.common.constant.Constants;
import com.mochu.common.exception.BusinessException;
import com.mochu.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 财务管理服务 — 对账单 / 付款申请 / 发票 / 报销 / 成本台账 / 收款
 */
@Service
@RequiredArgsConstructor
public class FinanceService {

    private final BizStatementMapper statementMapper;
    private final BizPaymentApplyMapper paymentApplyMapper;
    private final BizInvoiceMapper invoiceMapper;
    private final BizReimburseMapper reimburseMapper;
    private final BizCostLedgerMapper costLedgerMapper;
    private final BizReceiptMapper receiptMapper;
    private final NoGeneratorService noGeneratorService;

    // ====================== 对账单 ======================

    public PageResult<BizStatement> listStatements(Integer projectId, Integer contractId, String status,
                                                    Integer page, Integer size) {
        int p = (page == null || page < 1) ? Constants.DEFAULT_PAGE : page;
        int s = (size == null || size < 1) ? Constants.DEFAULT_SIZE : size;

        Page<BizStatement> pageParam = new Page<>(p, s);
        LambdaQueryWrapper<BizStatement> wrapper = new LambdaQueryWrapper<>();
        if (projectId != null) {
            wrapper.eq(BizStatement::getProjectId, projectId);
        }
        if (contractId != null) {
            wrapper.eq(BizStatement::getContractId, contractId);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(BizStatement::getStatus, status);
        }
        wrapper.orderByDesc(BizStatement::getCreatedAt);

        statementMapper.selectPage(pageParam, wrapper);
        return new PageResult<>(pageParam.getRecords(), pageParam.getTotal(), p, s);
    }

    public BizStatement getStatementById(Integer id) {
        return statementMapper.selectById(id);
    }

    public void createStatement(StatementDTO dto) {
        BizStatement entity = new BizStatement();
        BeanUtils.copyProperties(dto, entity);
        entity.setStatementNo(noGeneratorService.generate("ST"));
        entity.setStatus("draft");
        statementMapper.insert(entity);
    }

    public void updateStatement(Integer id, StatementDTO dto) {
        BizStatement entity = statementMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("对账单不存在");
        }
        BeanUtils.copyProperties(dto, entity, "id");
        statementMapper.updateById(entity);
    }

    public void updateStatementStatus(Integer id, String status) {
        BizStatement entity = statementMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("对账单不存在");
        }
        entity.setStatus(status);
        statementMapper.updateById(entity);
    }

    public void deleteStatement(Integer id) {
        statementMapper.deleteById(id);
    }

    // ====================== 付款申请 ======================

    public PageResult<BizPaymentApply> listPayments(Integer projectId, Integer contractId,
                                                     String paymentType, String status,
                                                     Integer page, Integer size) {
        int p = (page == null || page < 1) ? Constants.DEFAULT_PAGE : page;
        int s = (size == null || size < 1) ? Constants.DEFAULT_SIZE : size;

        Page<BizPaymentApply> pageParam = new Page<>(p, s);
        LambdaQueryWrapper<BizPaymentApply> wrapper = new LambdaQueryWrapper<>();
        if (projectId != null) {
            wrapper.eq(BizPaymentApply::getProjectId, projectId);
        }
        if (contractId != null) {
            wrapper.eq(BizPaymentApply::getContractId, contractId);
        }
        if (paymentType != null && !paymentType.isBlank()) {
            wrapper.eq(BizPaymentApply::getPaymentType, paymentType);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(BizPaymentApply::getStatus, status);
        }
        wrapper.orderByDesc(BizPaymentApply::getCreatedAt);

        paymentApplyMapper.selectPage(pageParam, wrapper);
        return new PageResult<>(pageParam.getRecords(), pageParam.getTotal(), p, s);
    }

    public BizPaymentApply getPaymentById(Integer id) {
        return paymentApplyMapper.selectById(id);
    }

    public void createPayment(PaymentApplyDTO dto) {
        BizPaymentApply entity = new BizPaymentApply();
        BeanUtils.copyProperties(dto, entity);
        entity.setPaymentNo(noGeneratorService.generate("PA"));
        entity.setStatus("draft");
        paymentApplyMapper.insert(entity);
    }

    public void updatePayment(Integer id, PaymentApplyDTO dto) {
        BizPaymentApply entity = paymentApplyMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("付款申请不存在");
        }
        BeanUtils.copyProperties(dto, entity, "id");
        paymentApplyMapper.updateById(entity);
    }

    public void updatePaymentStatus(Integer id, String status) {
        BizPaymentApply entity = paymentApplyMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("付款申请不存在");
        }
        entity.setStatus(status);
        paymentApplyMapper.updateById(entity);
    }

    public void deletePayment(Integer id) {
        paymentApplyMapper.deleteById(id);
    }

    // ====================== 发票 ======================

    public PageResult<BizInvoice> listInvoices(String bizType, Integer bizId, String invoiceType,
                                                String status, Integer page, Integer size) {
        int p = (page == null || page < 1) ? Constants.DEFAULT_PAGE : page;
        int s = (size == null || size < 1) ? Constants.DEFAULT_SIZE : size;

        Page<BizInvoice> pageParam = new Page<>(p, s);
        LambdaQueryWrapper<BizInvoice> wrapper = new LambdaQueryWrapper<>();
        if (bizType != null && !bizType.isBlank()) {
            wrapper.eq(BizInvoice::getBizType, bizType);
        }
        if (bizId != null) {
            wrapper.eq(BizInvoice::getBizId, bizId);
        }
        if (invoiceType != null && !invoiceType.isBlank()) {
            wrapper.eq(BizInvoice::getInvoiceType, invoiceType);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(BizInvoice::getStatus, status);
        }
        wrapper.orderByDesc(BizInvoice::getCreatedAt);

        invoiceMapper.selectPage(pageParam, wrapper);
        return new PageResult<>(pageParam.getRecords(), pageParam.getTotal(), p, s);
    }

    public BizInvoice getInvoiceById(Integer id) {
        return invoiceMapper.selectById(id);
    }

    public void createInvoice(InvoiceDTO dto) {
        BizInvoice entity = new BizInvoice();
        BeanUtils.copyProperties(dto, entity);
        // invoiceNo comes from user input (real invoice number), generate internal IV no
        entity.setInvoiceNo(dto.getInvoiceNo());
        entity.setStatus("active");
        invoiceMapper.insert(entity);
    }

    public void updateInvoice(Integer id, InvoiceDTO dto) {
        BizInvoice entity = invoiceMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("发票不存在");
        }
        BeanUtils.copyProperties(dto, entity, "id");
        invoiceMapper.updateById(entity);
    }

    public void updateInvoiceStatus(Integer id, String status) {
        BizInvoice entity = invoiceMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("发票不存在");
        }
        entity.setStatus(status);
        invoiceMapper.updateById(entity);
    }

    public void deleteInvoice(Integer id) {
        invoiceMapper.deleteById(id);
    }

    // ====================== 报销 ======================

    public PageResult<BizReimburse> listReimburses(Integer deptId, Integer projectId,
                                                    String reimburseType, String status,
                                                    Integer page, Integer size) {
        int p = (page == null || page < 1) ? Constants.DEFAULT_PAGE : page;
        int s = (size == null || size < 1) ? Constants.DEFAULT_SIZE : size;

        Page<BizReimburse> pageParam = new Page<>(p, s);
        LambdaQueryWrapper<BizReimburse> wrapper = new LambdaQueryWrapper<>();
        if (deptId != null) {
            wrapper.eq(BizReimburse::getDeptId, deptId);
        }
        if (projectId != null) {
            wrapper.eq(BizReimburse::getProjectId, projectId);
        }
        if (reimburseType != null && !reimburseType.isBlank()) {
            wrapper.eq(BizReimburse::getReimburseType, reimburseType);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(BizReimburse::getStatus, status);
        }
        wrapper.orderByDesc(BizReimburse::getCreatedAt);

        reimburseMapper.selectPage(pageParam, wrapper);
        return new PageResult<>(pageParam.getRecords(), pageParam.getTotal(), p, s);
    }

    public BizReimburse getReimburseById(Integer id) {
        return reimburseMapper.selectById(id);
    }

    public void createReimburse(ReimburseDTO dto) {
        BizReimburse entity = new BizReimburse();
        BeanUtils.copyProperties(dto, entity);
        entity.setReimburseNo(noGeneratorService.generate("RB"));
        entity.setStatus("draft");
        reimburseMapper.insert(entity);
    }

    public void updateReimburse(Integer id, ReimburseDTO dto) {
        BizReimburse entity = reimburseMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("报销单不存在");
        }
        BeanUtils.copyProperties(dto, entity, "id");
        reimburseMapper.updateById(entity);
    }

    public void updateReimburseStatus(Integer id, String status) {
        BizReimburse entity = reimburseMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("报销单不存在");
        }
        entity.setStatus(status);
        reimburseMapper.updateById(entity);
    }

    public void deleteReimburse(Integer id) {
        reimburseMapper.deleteById(id);
    }

    // ====================== 成本台账 ======================

    public PageResult<BizCostLedger> listCostLedger(Integer projectId, String costType,
                                                     String costSubtype, Integer page, Integer size) {
        int p = (page == null || page < 1) ? Constants.DEFAULT_PAGE : page;
        int s = (size == null || size < 1) ? Constants.DEFAULT_SIZE : size;

        Page<BizCostLedger> pageParam = new Page<>(p, s);
        LambdaQueryWrapper<BizCostLedger> wrapper = new LambdaQueryWrapper<>();
        if (projectId != null) {
            wrapper.eq(BizCostLedger::getProjectId, projectId);
        }
        if (costType != null && !costType.isBlank()) {
            wrapper.eq(BizCostLedger::getCostType, costType);
        }
        if (costSubtype != null && !costSubtype.isBlank()) {
            wrapper.eq(BizCostLedger::getCostSubtype, costSubtype);
        }
        wrapper.orderByDesc(BizCostLedger::getCreatedAt);

        costLedgerMapper.selectPage(pageParam, wrapper);
        return new PageResult<>(pageParam.getRecords(), pageParam.getTotal(), p, s);
    }

    public BizCostLedger getCostLedgerById(Integer id) {
        return costLedgerMapper.selectById(id);
    }

    public List<BizCostLedger> listCostLedgerByProject(Integer projectId) {
        return costLedgerMapper.selectList(
                new LambdaQueryWrapper<BizCostLedger>()
                        .eq(BizCostLedger::getProjectId, projectId)
                        .orderByDesc(BizCostLedger::getCollectTime));
    }

    // ====================== 收款 ======================

    public PageResult<BizReceipt> listReceipts(Integer projectId, Integer contractId, String status,
                                                Integer page, Integer size) {
        int p = (page == null || page < 1) ? Constants.DEFAULT_PAGE : page;
        int s = (size == null || size < 1) ? Constants.DEFAULT_SIZE : size;

        Page<BizReceipt> pageParam = new Page<>(p, s);
        LambdaQueryWrapper<BizReceipt> wrapper = new LambdaQueryWrapper<>();
        if (projectId != null) {
            wrapper.eq(BizReceipt::getProjectId, projectId);
        }
        if (contractId != null) {
            wrapper.eq(BizReceipt::getContractId, contractId);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(BizReceipt::getStatus, status);
        }
        wrapper.orderByDesc(BizReceipt::getCreatedAt);

        receiptMapper.selectPage(pageParam, wrapper);
        return new PageResult<>(pageParam.getRecords(), pageParam.getTotal(), p, s);
    }

    public BizReceipt getReceiptById(Integer id) {
        return receiptMapper.selectById(id);
    }

    public void createReceipt(ReceiptDTO dto) {
        BizReceipt entity = new BizReceipt();
        BeanUtils.copyProperties(dto, entity);
        entity.setReceiptNo(noGeneratorService.generate("SK"));
        entity.setStatus("draft");
        receiptMapper.insert(entity);
    }

    public void updateReceipt(Integer id, ReceiptDTO dto) {
        BizReceipt entity = receiptMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("收款单不存在");
        }
        BeanUtils.copyProperties(dto, entity, "id");
        receiptMapper.updateById(entity);
    }

    public void updateReceiptStatus(Integer id, String status) {
        BizReceipt entity = receiptMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("收款单不存在");
        }
        entity.setStatus(status);
        receiptMapper.updateById(entity);
    }

    public void deleteReceipt(Integer id) {
        receiptMapper.deleteById(id);
    }

    // ====================== 成本汇总 ======================

    public Map<String, BigDecimal> getCostSummary(Integer projectId) {
        List<BizCostLedger> list = costLedgerMapper.selectList(
                new LambdaQueryWrapper<BizCostLedger>()
                        .eq(BizCostLedger::getProjectId, projectId));
        return list.stream()
                .collect(Collectors.groupingBy(
                        BizCostLedger::getCostType,
                        Collectors.reducing(BigDecimal.ZERO, BizCostLedger::getAmount, BigDecimal::add)));
    }
}
