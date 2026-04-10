package com.mochu.business.controller;

import com.mochu.business.service.ReportService;
import com.mochu.common.result.R;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 报表汇总接口
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/project")
    @PreAuthorize("hasAuthority('report:view')")
    public R<Map<String, Object>> projectSummary() {
        return R.ok(reportService.getProjectSummary());
    }

    @GetMapping("/finance")
    @PreAuthorize("hasAuthority('report:view')")
    public R<Map<String, Object>> financeSummary() {
        return R.ok(reportService.getFinanceSummary());
    }

    @GetMapping("/inventory")
    @PreAuthorize("hasAuthority('report:view')")
    public R<Map<String, Object>> inventorySummary() {
        return R.ok(reportService.getInventorySummary());
    }

    @GetMapping("/contract")
    @PreAuthorize("hasAuthority('report:view')")
    public R<Map<String, Object>> contractSummary() {
        return R.ok(reportService.getContractSummary());
    }

    @GetMapping("/cost")
    @PreAuthorize("hasAuthority('report:view')")
    public R<Map<String, Object>> costSummary() {
        return R.ok(reportService.getCostSummary());
    }

    @GetMapping("/hr")
    @PreAuthorize("hasAuthority('report:view')")
    public R<Map<String, Object>> hrSummary() {
        return R.ok(reportService.getHrSummary());
    }
}
