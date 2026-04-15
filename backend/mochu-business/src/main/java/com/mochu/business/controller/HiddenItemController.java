package com.mochu.business.controller;

import com.mochu.business.dto.HiddenItemDTO;
import com.mochu.business.entity.BizHiddenItem;
import com.mochu.business.service.HiddenItemService;
import com.mochu.common.result.PageResult;
import com.mochu.common.result.R;
import com.mochu.framework.annotation.Idempotent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 暗项管理 Controller
 */
@RestController
@RequestMapping("/api/v1/hidden-items")
@RequiredArgsConstructor
public class HiddenItemController {

    private final HiddenItemService hiddenItemService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public R<PageResult<BizHiddenItem>> list(
            @RequestParam(required = false) Integer projectId,
            @RequestParam(required = false) Integer contractId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String itemType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return R.ok(hiddenItemService.list(projectId, contractId, status, itemType, page, size));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public R<BizHiddenItem> getById(@PathVariable Integer id) {
        return R.ok(hiddenItemService.getById(id));
    }

    @PostMapping
    @Idempotent
    @PreAuthorize("hasAuthority('project:view-all')")
    public R<Void> create(@Valid @RequestBody HiddenItemDTO dto) {
        hiddenItemService.create(dto);
        return R.ok();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('project:view-all')")
    public R<Void> update(@PathVariable Integer id, @Valid @RequestBody HiddenItemDTO dto) {
        hiddenItemService.update(id, dto);
        return R.ok();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('project:view-all')")
    public R<Void> updateStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        hiddenItemService.updateStatus(id, body.get("status"));
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('project:view-all')")
    public R<Void> delete(@PathVariable Integer id) {
        hiddenItemService.delete(id);
        return R.ok();
    }

    @GetMapping("/summary")
    @PreAuthorize("isAuthenticated()")
    public R<Map<String, Object>> summary(@RequestParam Integer projectId) {
        return R.ok(hiddenItemService.summary(projectId));
    }
}
