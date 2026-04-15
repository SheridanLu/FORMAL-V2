package com.mochu.business.controller;

import com.mochu.business.dto.CollectionFollowUpDTO;
import com.mochu.business.dto.ReceiptPlanDTO;
import com.mochu.business.entity.BizCollectionFollowUp;
import com.mochu.business.entity.BizReceiptPlan;
import com.mochu.business.service.CollectionSupervisionService;
import com.mochu.common.result.PageResult;
import com.mochu.common.result.R;
import com.mochu.framework.annotation.Idempotent;
import com.mochu.framework.annotation.AuditLog;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 回款督办 Controller
 */
@RestController
@RequestMapping("/api/v1/collection")
@RequiredArgsConstructor
public class CollectionSupervisionController {

    private final CollectionSupervisionService collectionService;

    // ==================== 收款计划 ====================

    @GetMapping("/plans")
    @PreAuthorize("isAuthenticated()")
    public R<PageResult<BizReceiptPlan>> listPlans(
            @RequestParam(required = false) Integer contractId,
            @RequestParam(required = false) Integer projectId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return R.ok(collectionService.listPlans(contractId, projectId, status, page, size));
    }

    @AuditLog(operateType = "CREATE", operateModule = "回款督办", bizType = "receipt_plan")
    @PostMapping("/plans")
    @Idempotent
    @PreAuthorize("hasAuthority('finance:receipt')")
    public R<Void> createPlan(@Valid @RequestBody ReceiptPlanDTO dto) {
        collectionService.createPlan(dto);
        return R.ok();
    }

    @AuditLog(operateType = "UPDATE", operateModule = "回款督办", bizType = "receipt_plan", saveBefore = true)
    @PutMapping("/plans/{id}")
    @PreAuthorize("hasAuthority('finance:receipt')")
    public R<Void> updatePlan(@PathVariable Integer id, @Valid @RequestBody ReceiptPlanDTO dto) {
        collectionService.updatePlan(id, dto);
        return R.ok();
    }

    @AuditLog(operateType = "STATUS_CHANGE", operateModule = "回款督办", bizType = "receipt_plan")
    @PatchMapping("/plans/{id}/confirm")
    @PreAuthorize("hasAuthority('finance:receipt')")
    public R<Void> confirmReceipt(@PathVariable Integer id,
                                   @RequestParam BigDecimal actualAmount,
                                   @RequestParam(required = false) LocalDate actualDate) {
        collectionService.confirmReceipt(id, actualAmount, actualDate);
        return R.ok();
    }

    @AuditLog(operateType = "DELETE", operateModule = "回款督办", bizType = "receipt_plan", saveBefore = true)
    @DeleteMapping("/plans/{id}")
    @PreAuthorize("hasAuthority('finance:receipt')")
    public R<Void> deletePlan(@PathVariable Integer id) {
        collectionService.deletePlan(id);
        return R.ok();
    }

    @GetMapping("/overdue-count")
    @PreAuthorize("isAuthenticated()")
    public R<Map<String, Long>> overdueCount() {
        return R.ok(Map.of("count", collectionService.countOverdue()));
    }

    // ==================== 跟进记录 ====================

    @GetMapping("/plans/{planId}/follow-ups")
    @PreAuthorize("isAuthenticated()")
    public R<List<BizCollectionFollowUp>> listFollowUps(@PathVariable Integer planId) {
        return R.ok(collectionService.listFollowUps(planId));
    }

    @AuditLog(operateType = "CREATE", operateModule = "回款督办", bizType = "collection_follow_up")
    @PostMapping("/follow-ups")
    @Idempotent
    @PreAuthorize("hasAuthority('finance:receipt')")
    public R<Void> createFollowUp(@Valid @RequestBody CollectionFollowUpDTO dto) {
        collectionService.createFollowUp(dto);
        return R.ok();
    }
}
