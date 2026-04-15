package com.mochu.business.controller;

import com.mochu.business.dto.CompanyInfoDTO;
import com.mochu.business.entity.BizCompanyInfo;
import com.mochu.business.service.CompanyInfoService;
import com.mochu.framework.annotation.Idempotent;
import com.mochu.framework.annotation.AuditLog;
import com.mochu.common.result.PageResult;
import com.mochu.common.result.R;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公司/单位信息 Controller
 *
 * <p>V3.0 §3.5: 提供公司信息 CRUD + 搜索接口，用于前端自动填充
 */
@RestController
@RequestMapping("/api/v1/company-info")
@RequiredArgsConstructor
public class CompanyInfoController {

    private final CompanyInfoService companyInfoService;

    /**
     * 分页列表
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public R<PageResult<BizCompanyInfo>> list(
            @RequestParam(required = false) String companyName,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return R.ok(companyInfoService.list(companyName, page, size));
    }

    /**
     * 模糊搜索（自动填充下拉）
     */
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public R<List<BizCompanyInfo>> search(@RequestParam(required = false) String keyword) {
        return R.ok(companyInfoService.search(keyword));
    }

    /**
     * 按名称精确查找（自动填充回填详情）
     */
    @GetMapping("/by-name")
    @PreAuthorize("isAuthenticated()")
    public R<BizCompanyInfo> getByName(@RequestParam String name) {
        return R.ok(companyInfoService.getByName(name));
    }

    /**
     * 详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public R<BizCompanyInfo> detail(@PathVariable Integer id) {
        return R.ok(companyInfoService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    @Idempotent
    @AuditLog(operateType = "CREATE", operateModule = "公司信息", bizType = "company_info")
    @PreAuthorize("hasAuthority('system:company-info')")
    public R<Void> create(@Valid @RequestBody CompanyInfoDTO dto) {
        companyInfoService.create(dto);
        return R.ok();
    }

    /**
     * 更新
     */
    @PutMapping("/{id}")
    @Idempotent
    @AuditLog(operateType = "UPDATE", operateModule = "公司信息", bizType = "company_info", saveBefore = true)
    @PreAuthorize("hasAuthority('system:company-info')")
    public R<Void> update(@PathVariable Integer id, @Valid @RequestBody CompanyInfoDTO dto) {
        companyInfoService.update(id, dto);
        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    @AuditLog(operateType = "DELETE", operateModule = "公司信息", bizType = "company_info", saveBefore = true)
    @PreAuthorize("hasAuthority('system:company-info')")
    public R<Void> delete(@PathVariable Integer id) {
        companyInfoService.delete(id);
        return R.ok();
    }
}
