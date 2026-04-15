package com.mochu.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochu.common.constant.Constants;
import com.mochu.common.exception.BusinessException;
import com.mochu.common.result.PageResult;
import com.mochu.common.security.SecurityUtils;
import com.mochu.system.entity.SysTodo;
import com.mochu.system.mapper.SysTodoMapper;
import com.mochu.system.vo.TodoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 待办服务 — 对照 V3.2 §5.9.2
 */
@Service
@RequiredArgsConstructor
public class TodoService {

    private final SysTodoMapper todoMapper;

    /**
     * 当前用户待办列表（分页）
     */
    public PageResult<TodoVO> listMyTodos(Integer status, Integer page, Integer size) {
        Integer userId = SecurityUtils.getCurrentUserId();
        if (page == null || page < 1) page = Constants.DEFAULT_PAGE;
        if (size == null || size < 1) size = Constants.DEFAULT_SIZE;

        Page<SysTodo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SysTodo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysTodo::getUserId, userId);
        if (status != null) {
            wrapper.eq(SysTodo::getStatus, status);
        }
        wrapper.orderByDesc(SysTodo::getCreatedAt);

        todoMapper.selectPage(pageParam, wrapper);
        List<TodoVO> voList = pageParam.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return new PageResult<>(voList, pageParam.getTotal(), page, size);
    }

    /**
     * 当前用户待办数量
     */
    public long countPending() {
        Integer userId = SecurityUtils.getCurrentUserId();
        return todoMapper.selectCount(
                new LambdaQueryWrapper<SysTodo>()
                        .eq(SysTodo::getUserId, userId)
                        .eq(SysTodo::getStatus, 0)
        );
    }

    /**
     * 标记已处理
     */
    public void markDone(Integer id) {
        SysTodo todo = todoMapper.selectById(id);
        if (todo == null) {
            throw new BusinessException(404, "待办不存在");
        }
        Integer userId = SecurityUtils.getCurrentUserId();
        if (!todo.getUserId().equals(userId)) {
            throw new BusinessException(403, "只能处理自己的待办");
        }
        todo.setStatus(1);
        todo.setReadAt(java.time.LocalDateTime.now());
        todoMapper.updateById(todo);
    }

    /**
     * V3.0: 待办详情（含源单据跳转信息）
     */
    public TodoVO getById(Integer id) {
        SysTodo todo = todoMapper.selectById(id);
        if (todo == null) {
            throw new BusinessException(404, "待办不存在");
        }
        Integer userId = SecurityUtils.getCurrentUserId();
        if (!todo.getUserId().equals(userId)) {
            throw new BusinessException(403, "只能查看自己的待办");
        }
        return toVO(todo);
    }

    /**
     * 创建待办（内部方法，供业务模块调用）
     */
    public void createTodo(Integer userId, String bizType, Integer bizId, String title, String content) {
        SysTodo todo = new SysTodo();
        todo.setUserId(userId);
        todo.setBizType(bizType);
        todo.setBizId(bizId);
        todo.setTitle(title);
        todo.setContent(content);
        todo.setStatus(0);
        todoMapper.insert(todo);
    }

    /**
     * 按业务类型+单据ID批量标记已处理（审批流转时调用）
     */
    public void markDoneByBiz(String bizType, Integer bizId) {
        List<SysTodo> todos = todoMapper.selectList(
                new LambdaQueryWrapper<SysTodo>()
                        .eq(SysTodo::getBizType, bizType)
                        .eq(SysTodo::getBizId, bizId)
                        .eq(SysTodo::getStatus, 0));
        for (SysTodo t : todos) {
            t.setStatus(1);
            todoMapper.updateById(t);
        }
    }

    /**
     * 按用户+业务类型+单据ID标记已处理
     */
    public void markDoneByUserAndBiz(Integer userId, String bizType, Integer bizId) {
        List<SysTodo> todos = todoMapper.selectList(
                new LambdaQueryWrapper<SysTodo>()
                        .eq(SysTodo::getUserId, userId)
                        .eq(SysTodo::getBizType, bizType)
                        .eq(SysTodo::getBizId, bizId)
                        .eq(SysTodo::getStatus, 0));
        for (SysTodo t : todos) {
            t.setStatus(1);
            todoMapper.updateById(t);
        }
    }

    private TodoVO toVO(SysTodo entity) {
        TodoVO vo = new TodoVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
