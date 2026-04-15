package com.mochu.system.controller;

import com.mochu.common.result.PageResult;
import com.mochu.common.result.R;
import com.mochu.framework.annotation.Idempotent;
import com.mochu.framework.annotation.AuditLog;
import com.mochu.system.dto.BannerDTO;
import com.mochu.system.entity.SysBanner;
import com.mochu.system.service.BannerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 首页轮播图 Controller
 */
@RestController
@RequestMapping("/api/v1/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    /**
     * 管理端分页列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('system:banner-manage')")
    public R<PageResult<SysBanner>> list(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return R.ok(bannerService.list(title, status, page, size));
    }

    /**
     * 前台展示列表（认证用户即可）
     */
    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    public R<List<SysBanner>> listActive() {
        return R.ok(bannerService.listActive());
    }

    /**
     * 详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:banner-manage')")
    public R<SysBanner> detail(@PathVariable Integer id) {
        return R.ok(bannerService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    @Idempotent
    @AuditLog(operateType = "CREATE", operateModule = "横幅管理", bizType = "banner")
    @PreAuthorize("hasAuthority('system:banner-manage')")
    public R<Void> create(@Valid @RequestBody BannerDTO dto) {
        bannerService.create(dto);
        return R.ok();
    }

    /**
     * 更新
     */
    @PutMapping("/{id}")
    @Idempotent
    @AuditLog(operateType = "UPDATE", operateModule = "横幅管理", bizType = "banner", saveBefore = true)
    @PreAuthorize("hasAuthority('system:banner-manage')")
    public R<Void> update(@PathVariable Integer id, @Valid @RequestBody BannerDTO dto) {
        bannerService.update(id, dto);
        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    @AuditLog(operateType = "DELETE", operateModule = "横幅管理", bizType = "banner", saveBefore = true)
    @PreAuthorize("hasAuthority('system:banner-manage')")
    public R<Void> delete(@PathVariable Integer id) {
        bannerService.delete(id);
        return R.ok();
    }

    /**
     * 切换启用/禁用
     */
    @PatchMapping("/{id}/toggle")
    @AuditLog(operateType = "STATUS_CHANGE", operateModule = "横幅管理", bizType = "banner")
    @PreAuthorize("hasAuthority('system:banner-manage')")
    public R<Void> toggle(@PathVariable Integer id) {
        bannerService.toggleStatus(id);
        return R.ok();
    }
}
