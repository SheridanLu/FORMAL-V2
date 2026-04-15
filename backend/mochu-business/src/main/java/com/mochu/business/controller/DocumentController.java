package com.mochu.business.controller;

import com.mochu.business.dto.DocumentDTO;
import com.mochu.business.entity.BizDocument;
import com.mochu.business.service.DocumentService;
import com.mochu.common.result.PageResult;
import com.mochu.common.result.R;
import com.mochu.framework.annotation.AuditLog;
import com.mochu.framework.annotation.Idempotent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文档管理 Controller
 *
 * <p>V3.2: 文档上传、分类查询、下载、删除
 */
@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    /**
     * 分页查询文档列表
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public R<PageResult<BizDocument>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, name = "project_id") Integer projectId,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return R.ok(documentService.list(keyword, projectId, category, page, size));
    }

    /**
     * 文档详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public R<BizDocument> detail(@PathVariable Integer id) {
        return R.ok(documentService.getById(id));
    }

    /**
     * 上传文档（multipart/form-data）
     */
    @PostMapping("/upload")
    @Idempotent
    @PreAuthorize("hasAnyAuthority('doc:upload', 'doc:manage')")
    @AuditLog(operateType = "CREATE", operateModule = "文档管理", bizType = "document")
    public R<BizDocument> upload(@RequestParam("file") MultipartFile file,
                                 @RequestParam(defaultValue = "other") String category,
                                 @RequestParam(required = false, name = "project_id") Integer projectId) throws Exception {
        return R.ok(documentService.upload(file, category, projectId));
    }

    /**
     * 手动创建文档记录
     */
    @PostMapping
    @Idempotent
    @PreAuthorize("hasAnyAuthority('doc:upload', 'doc:manage')")
    @AuditLog(operateType = "CREATE", operateModule = "文档管理", bizType = "document")
    public R<Void> create(@Valid @RequestBody DocumentDTO dto) {
        documentService.create(dto);
        return R.ok();
    }

    /**
     * 更新文档元信息
     */
    @PutMapping("/{id}")
    @Idempotent
    @PreAuthorize("hasAnyAuthority('doc:upload', 'doc:manage')")
    @AuditLog(operateType = "UPDATE", operateModule = "文档管理", bizType = "document", saveBefore = true)
    public R<Void> update(@PathVariable Integer id, @Valid @RequestBody DocumentDTO dto) {
        documentService.update(id, dto);
        return R.ok();
    }

    /**
     * 删除文档
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('doc:upload', 'doc:manage')")
    @AuditLog(operateType = "DELETE", operateModule = "文档管理", bizType = "document", saveBefore = true)
    public R<Void> delete(@PathVariable Integer id) {
        documentService.delete(id);
        return R.ok();
    }
}
