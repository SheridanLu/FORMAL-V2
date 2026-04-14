package com.mochu.system.controller;

import com.mochu.common.result.R;
import com.mochu.system.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 通讯录接口 — P5 §2
 */
@RestController
@RequestMapping("/api/v1/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    /** 获取部门树 */
    @GetMapping("/depts")
    public R<List<Map<String, Object>>> getDeptTree() {
        return R.ok(contactService.getDeptTree());
    }

    /** 按部门查询员工列表 */
    @GetMapping("/employees")
    public R<List<Map<String, Object>>> getEmployees(
            @RequestParam(required = false) Integer deptId) {
        return R.ok(contactService.getEmployees(deptId));
    }

    /** 搜索员工 */
    @GetMapping("/search")
    public R<List<Map<String, Object>>> search(@RequestParam String keyword) {
        return R.ok(contactService.searchEmployees(keyword));
    }
}
