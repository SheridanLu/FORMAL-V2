package com.mochu.business.controller;

import com.mochu.business.entity.BizGlodonImport;
import com.mochu.business.service.GlodonImportService;
import com.mochu.common.result.PageResult;
import com.mochu.common.result.R;
import com.mochu.framework.annotation.Idempotent;
import com.mochu.framework.annotation.AuditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 广联达数据导入 Controller
 */
@RestController
@RequestMapping("/api/v1/glodon")
@RequiredArgsConstructor
public class GlodonImportController {

    private final GlodonImportService glodonImportService;

    @GetMapping("/imports")
    @PreAuthorize("isAuthenticated()")
    public R<PageResult<BizGlodonImport>> list(
            @RequestParam(required = false) Integer projectId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return R.ok(glodonImportService.list(projectId, page, size));
    }

    @AuditLog(operateType = "IMPORT", operateModule = "广联达导入", bizType = "glodon_import")
    @PostMapping("/import")
    @Idempotent
    @PreAuthorize("hasAuthority('project:view-all')")
    public R<BizGlodonImport> importFile(
            @RequestParam Integer projectId,
            @RequestParam(defaultValue = "cost") String importType,
            @RequestPart MultipartFile file) {
        return R.ok(glodonImportService.importExcel(projectId, importType, file));
    }

    @AuditLog(operateType = "DELETE", operateModule = "广联达导入", bizType = "glodon_import", saveBefore = true)
    @DeleteMapping("/imports/{id}")
    @PreAuthorize("hasAuthority('project:view-all')")
    public R<Void> deleteImport(@PathVariable Integer id) {
        glodonImportService.deleteImport(id);
        return R.ok();
    }
}
