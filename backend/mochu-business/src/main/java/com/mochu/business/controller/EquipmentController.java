package com.mochu.business.controller;

import com.mochu.business.dto.EquipmentDTO;
import com.mochu.business.entity.BizEquipment;
import com.mochu.business.service.EquipmentService;
import com.mochu.common.result.PageResult;
import com.mochu.common.result.R;
import com.mochu.framework.annotation.Idempotent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 设备管理 Controller
 *
 * <p>V3.0: 设备台账 CRUD + 分配 + 状态变更
 */
@RestController
@RequestMapping("/api/v1/equipments")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public R<PageResult<BizEquipment>> list(
            @RequestParam(required = false) String equipmentName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer projectId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return R.ok(equipmentService.list(equipmentName, category, status, projectId, page, size));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public R<BizEquipment> detail(@PathVariable Integer id) {
        return R.ok(equipmentService.getById(id));
    }

    @PostMapping
    @Idempotent
    @PreAuthorize("hasAuthority('equipment:manage')")
    public R<Void> create(@Valid @RequestBody EquipmentDTO dto) {
        equipmentService.create(dto);
        return R.ok();
    }

    @PutMapping("/{id}")
    @Idempotent
    @PreAuthorize("hasAuthority('equipment:manage')")
    public R<Void> update(@PathVariable Integer id, @Valid @RequestBody EquipmentDTO dto) {
        equipmentService.update(id, dto);
        return R.ok();
    }

    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAuthority('equipment:manage')")
    public R<Void> assign(@PathVariable Integer id,
                           @RequestParam Integer projectId,
                           @RequestParam(required = false) String location) {
        equipmentService.assignToProject(id, projectId, location);
        return R.ok();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('equipment:manage')")
    public R<Void> updateStatus(@PathVariable Integer id, @RequestParam String status) {
        equipmentService.updateStatus(id, status);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('equipment:manage')")
    public R<Void> delete(@PathVariable Integer id) {
        equipmentService.delete(id);
        return R.ok();
    }
}
