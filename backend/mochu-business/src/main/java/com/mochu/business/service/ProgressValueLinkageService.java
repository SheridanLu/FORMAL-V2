package com.mochu.business.service;

import com.mochu.business.entity.BizGanttTask;
import com.mochu.business.entity.BizStatement;
import com.mochu.business.mapper.BizGanttTaskMapper;
import com.mochu.common.exception.BusinessException;
import com.mochu.system.entity.SysTodo;
import com.mochu.system.mapper.SysTodoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgressValueLinkageService {

    private final BizGanttTaskMapper ganttTaskMapper;
    private final SysTodoMapper todoMapper;

    /** 弱相关预警阈值(%) */
    private static final BigDecimal WEAK_THRESHOLD = new BigDecimal("5");
    /** 强相关拦截阈值(%) */
    private static final BigDecimal STRONG_THRESHOLD = new BigDecimal("2");

    /**
     * 弱相关校验 — 对账单提交时调用
     * 产值比例低于进度比例超过5% → 预警但不阻止
     */
    public void weakCheck(Integer projectId, BigDecimal valueRatio,
                           BigDecimal progressRatio, Integer managerId) {
        BigDecimal diff = progressRatio.subtract(valueRatio);
        if (diff.compareTo(WEAK_THRESHOLD) > 0) {
            log.warn("进度-产值弱相关预警: projectId={}, 进度{}% vs 产值{}%, 偏差{}%",
                    projectId, progressRatio, valueRatio, diff);
            // 创建预警待办
            SysTodo todo = new SysTodo();
            todo.setUserId(managerId);
            todo.setTitle(String.format("【进度-产值预警】产值落后进度%.2f%%", diff));
            todo.setContent(String.format("当前进度%.2f%%，产值%.2f%%，偏差%.2f%%超过阈值5%%",
                    progressRatio, valueRatio, diff));
            todo.setBizType("progress_value_warning");
            todo.setBizId(projectId);
            todo.setStatus(0);
            todoMapper.insert(todo);
        }
    }

    /**
     * 强相关校验 — 劳务提报工程量时调用
     * 超进度 > 2% → 系统拦截
     */
    public void strongCheck(Integer projectId, BigDecimal reportedRatio,
                             BigDecimal progressRatio) {
        BigDecimal excess = reportedRatio.subtract(progressRatio);
        if (excess.compareTo(STRONG_THRESHOLD) > 0) {
            throw new BusinessException(400,
                    String.format("提报工程量(%.2f%%)超出进度(%.2f%%) %.2f%%，超过2%%限制",
                            reportedRatio, progressRatio, excess));
        }
    }
}
