package com.mochu.business.controller;

import com.mochu.business.dto.ContractPaymentPlanDTO;
import com.mochu.business.entity.BizContractPaymentPlan;
import com.mochu.business.service.ContractPaymentPlanService;
import com.mochu.common.result.R;
import com.mochu.common.security.SecurityUtils;
import com.mochu.framework.annotation.Idempotent;
import com.mochu.framework.annotation.AuditLog;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/contracts/{contractId}/payment-plans")
@RequiredArgsConstructor
public class ContractPaymentPlanController {

    private final ContractPaymentPlanService paymentPlanService;

    // ======================== 查询 ========================

    /**
     * 查询合同的付款计划列表
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('contract:view-all','contract:view-own')")
    public R<List<BizContractPaymentPlan>> list(@PathVariable Integer contractId) {
        return R.ok(paymentPlanService.listByContractId(contractId));
    }

    // ======================== 写操作 ========================

    /**
     * 创建 / 重新生成付款计划（批量替换）
     */
    @Idempotent
    @AuditLog(operateType = "CREATE", operateModule = "合同管理", bizType = "payment_plan")
    @PostMapping("hasAnyAuthority('contract:sign-income','contract:sign-expense')")
    public R<Void> create(@PathVariable Integer contractId,
                          @Valid @RequestBody List<ContractPaymentPlanDTO> plans) {
        Integer userId = SecurityUtils.getCurrentUserId();
        paymentPlanService.create(contractId, plans, userId);
        return R.ok();
    }
}
