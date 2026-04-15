package com.mochu.system.controller;

import com.mochu.common.result.PageResult;
import com.mochu.common.result.R;
import com.mochu.framework.annotation.Idempotent;
import com.mochu.framework.annotation.AuditLog;
import com.mochu.system.service.TodoService;
import com.mochu.system.vo.TodoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 待办中心接口 — 对照 V3.2 §5.9.2
 */
@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    /**
     * 我的待办列表 — GET /api/v1/todos
     */
    @GetMapping
    public R<PageResult<TodoVO>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return R.ok(todoService.listMyTodos(status, page, size));
    }

    /**
     * 待办数量 — GET /api/v1/todos/count
     */
    @GetMapping("/count")
    public R<Long> count() {
        return R.ok(todoService.countPending());
    }

    /**
     * V3.0: 待办详情 — GET /api/v1/todos/{id}
     */
    @GetMapping("/{id}")
    public R<TodoVO> detail(@PathVariable Integer id) {
        return R.ok(todoService.getById(id));
    }

    /**
     * 标记已处理 — PATCH /api/v1/todos/{id}/done
     */
    @Idempotent
    @AuditLog(operateType = "STATUS_CHANGE", operateModule = "待办管理", bizType = "todo")
    @PatchMapping("/{id}/done")
    public R<Void> markDone(@PathVariable Integer id) {
        todoService.markDone(id);
        return R.ok();
    }
}
