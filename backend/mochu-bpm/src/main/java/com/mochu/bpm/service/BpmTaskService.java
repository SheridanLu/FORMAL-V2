package com.mochu.bpm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mochu.bpm.dto.TaskActionDTO;
import com.mochu.bpm.entity.BpmTaskExt;
import com.mochu.bpm.mapper.BpmTaskExtMapper;
import com.mochu.bpm.vo.BpmTaskVO;
import com.mochu.common.exception.BusinessException;
import com.mochu.common.result.PageResult;
import com.mochu.common.security.SecurityUtils;
import com.mochu.system.entity.SysUser;
import com.mochu.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程任务服务 — 待办/已办/操作
 */
@Service
@RequiredArgsConstructor
public class BpmTaskService {

    private final TaskService taskService;
    private final RuntimeService runtimeService;
    private final BpmTaskExtMapper taskExtMapper;
    private final SysUserMapper sysUserMapper;

    /**
     * 当前用户待办任务分页
     */
    public PageResult<BpmTaskVO> listTodoTasks(Integer page, Integer size) {
        int p = (page == null || page < 1) ? 1 : page;
        int s = (size == null || size < 1) ? 10 : size;
        String userId = String.valueOf(SecurityUtils.getCurrentUserId());

        TaskQuery query = taskService.createTaskQuery()
                .taskCandidateOrAssigned(userId)
                .orderByTaskCreateTime().desc();
        long total = query.count();
        List<Task> tasks = query.listPage((p - 1) * s, s);
        return new PageResult<>(buildTaskVOs(tasks), total, p, s);
    }

    /**
     * 签收任务（候选人 → 指定处理人）
     */
    public void claimTask(String taskId) {
        String userId = String.valueOf(SecurityUtils.getCurrentUserId());
        Task task = getTask(taskId);
        if (task.getAssignee() != null && !task.getAssignee().equals(userId)) {
            throw new BusinessException("该任务已被他人签收");
        }
        taskService.claim(taskId, userId);
    }

    /**
     * 完成任务（通过）
     */
    @Transactional
    public void completeTask(String taskId, TaskActionDTO dto) {
        Task task = getTask(taskId);
        if (dto.getComment() != null && !dto.getComment().isBlank()) {
            taskService.addComment(taskId, task.getProcessInstanceId(), dto.getComment());
        }
        taskService.complete(taskId);
        // 检查流程是否结束
        updateExtResultIfEnded(task.getProcessInstanceId(), 1);
    }

    /**
     * 驳回任务（结束流程）
     */
    @Transactional
    public void rejectTask(String taskId, TaskActionDTO dto) {
        Task task = getTask(taskId);
        if (dto.getComment() != null) {
            taskService.addComment(taskId, task.getProcessInstanceId(), dto.getComment());
        }
        runtimeService.deleteProcessInstance(task.getProcessInstanceId(),
                "rejected:" + (dto.getComment() != null ? dto.getComment() : ""));
        updateTaskExtResult(task.getProcessInstanceId(), 2);
    }

    /**
     * 转办任务（更换处理人）
     */
    public void transferTask(String taskId, TaskActionDTO dto) {
        if (dto.getTargetUserId() == null) throw new BusinessException("请指定转办目标用户");
        getTask(taskId);
        taskService.setAssignee(taskId, String.valueOf(dto.getTargetUserId()));
        taskService.addComment(taskId, null, "转办给用户" + dto.getTargetUserId() +
                (dto.getComment() != null ? "：" + dto.getComment() : ""));
    }

    /**
     * 委派任务（委托他人处理，处理后回到委派人）
     */
    public void delegateTask(String taskId, TaskActionDTO dto) {
        if (dto.getTargetUserId() == null) throw new BusinessException("请指定委派目标用户");
        getTask(taskId);
        taskService.delegateTask(taskId, String.valueOf(dto.getTargetUserId()));
    }

    /**
     * 撤回流程（发起人撤回进行中的流程）
     */
    @Transactional
    public void withdrawProcess(String processInstId, String reason) {
        BpmTaskExt ext = taskExtMapper.selectOne(
                new LambdaQueryWrapper<BpmTaskExt>()
                        .eq(BpmTaskExt::getProcessInstId, processInstId));
        if (ext == null) throw new BusinessException("流程实例不存在");
        Integer currentUserId = SecurityUtils.getCurrentUserId();
        if (!currentUserId.equals(ext.getInitiatorId())) {
            throw new BusinessException("只有发起人才能撤回流程");
        }
        runtimeService.deleteProcessInstance(processInstId, "withdrawn:" + reason);
        updateTaskExtResult(processInstId, 3);
    }

    // ==================== 内部方法 ====================

    private Task getTask(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) throw new BusinessException("任务不存在或已处理");
        return task;
    }

    private List<BpmTaskVO> buildTaskVOs(List<Task> tasks) {
        if (tasks.isEmpty()) return List.of();
        // 批量查扩展信息
        List<String> instIds = tasks.stream().map(Task::getProcessInstanceId).distinct().collect(Collectors.toList());
        Map<String, BpmTaskExt> extMap = taskExtMapper.selectList(
                new LambdaQueryWrapper<BpmTaskExt>().in(BpmTaskExt::getProcessInstId, instIds))
                .stream().collect(Collectors.toMap(BpmTaskExt::getProcessInstId, e -> e));
        // 批量查用户
        List<Integer> assigneeIds = tasks.stream()
                .filter(t -> t.getAssignee() != null)
                .map(t -> { try { return Integer.parseInt(t.getAssignee()); } catch (Exception e) { return null; } })
                .filter(id -> id != null).distinct().collect(Collectors.toList());
        Map<Integer, String> userNameMap = assigneeIds.isEmpty() ? Map.of() :
                sysUserMapper.selectBatchIds(assigneeIds).stream()
                        .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName));

        return tasks.stream().map(t -> {
            BpmTaskVO vo = new BpmTaskVO();
            vo.setTaskId(t.getId());
            vo.setTaskName(t.getName());
            vo.setProcessInstId(t.getProcessInstanceId());
            vo.setProcessDefKey(t.getProcessDefinitionId());
            vo.setCreateTime(t.getCreateTime() != null ?
                    t.getCreateTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null);
            if (t.getAssignee() != null) {
                try {
                    int uid = Integer.parseInt(t.getAssignee());
                    vo.setAssigneeId(uid);
                    vo.setAssigneeName(userNameMap.getOrDefault(uid, t.getAssignee()));
                } catch (Exception ignored) {}
            }
            BpmTaskExt ext = extMap.get(t.getProcessInstanceId());
            if (ext != null) {
                vo.setBizType(ext.getBizType());
                vo.setBizId(ext.getBizId());
                vo.setBizNo(ext.getBizNo());
                vo.setInitiatorId(ext.getInitiatorId());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    private void updateExtResultIfEnded(String processInstId, int result) {
        // 如果流程实例已不存在（结束）则更新结果
        long running = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstId).count();
        if (running == 0) {
            updateTaskExtResult(processInstId, result);
        }
    }

    private void updateTaskExtResult(String processInstId, int result) {
        BpmTaskExt ext = taskExtMapper.selectOne(
                new LambdaQueryWrapper<BpmTaskExt>()
                        .eq(BpmTaskExt::getProcessInstId, processInstId));
        if (ext != null) {
            ext.setResult(result);
            ext.setEndTime(java.time.LocalDateTime.now());
            taskExtMapper.updateById(ext);
        }
    }
}
