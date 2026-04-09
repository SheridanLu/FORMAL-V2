package com.mochu.system.controller;

import com.mochu.common.result.R;
import com.mochu.system.service.PermissionService;
import com.mochu.system.vo.PermissionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 权限查询接口 — 对照 V3.2 §5.9.5
 */
@RestController
@RequestMapping("/api/v1/admin/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * 权限列表 — GET /api/v1/admin/permissions
     * 可选参数 module 按模块过滤
     */
    @GetMapping
    @PreAuthorize("hasAuthority('system:role-manage')")
    public R<List<PermissionVO>> list(@RequestParam(required = false) String module) {
        if (module != null && !module.isBlank()) {
            return R.ok(permissionService.listByModule(module));
        }
        return R.ok(permissionService.listAll());
    }

    /**
     * 权限树 — GET /api/v1/admin/permissions/tree
     * 按模块分组返回树形结构
     */
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:role-manage')")
    public R<List<Map<String, Object>>> tree() {
        return R.ok(permissionService.listTree());
    }
}
