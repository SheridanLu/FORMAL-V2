package com.mochu.business.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mochu.business.entity.BizReportSubscribe;
import com.mochu.business.mapper.BizReportSubscribeMapper;
import com.mochu.common.message.EmailSender;
import com.mochu.system.entity.SysTodo;
import com.mochu.system.entity.SysUser;
import com.mochu.system.mapper.SysTodoMapper;
import com.mochu.system.mapper.SysUserMapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * 周报月报推送
 * 周报 Cron: 0 45 6 ? * MON — 每周一 06:45
 * 月报 Cron: 0 0 7 1 * ? — 每月1日 07:00
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WeeklyMonthlyReportPushScheduler {

    private final BizReportSubscribeMapper subscribeMapper;
    private final SysTodoMapper todoMapper;
    private final SysUserMapper userMapper;
    private final EmailSender emailSender;

    @XxlJob("weeklyReportPushJob")
    public void pushWeeklyReports() {
        pushReports("weekly", "周报");
    }

    @XxlJob("monthlyReportPushJob")
    public void pushMonthlyReports() {
        pushReports("monthly", "月报");
    }

    private void pushReports(String frequency, String label) {
        try {
            List<BizReportSubscribe> subs = subscribeMapper.selectList(
                    new LambdaQueryWrapper<BizReportSubscribe>()
                            .eq(BizReportSubscribe::getFrequency, frequency)
                            .eq(BizReportSubscribe::getStatus, 1));

            for (BizReportSubscribe sub : subs) {
                String reportTitle = String.format("[%s] %s报表已更新", label, sub.getReportType());
                String reportContent = "请前往报表模块查看最新数据";

                SysTodo todo = new SysTodo();
                todo.setUserId(sub.getUserId());
                todo.setTitle(reportTitle);
                todo.setContent(reportContent);
                todo.setBizType("report_push");
                todo.setBizId(sub.getId());
                todo.setStatus(0);
                todoMapper.insert(todo);

                // V3.2: 订阅报表邮件推送
                try {
                    SysUser user = userMapper.selectById(sub.getUserId());
                    if (user != null && user.getEmail() != null && !user.getEmail().isBlank()) {
                        emailSender.send(user.getEmail(),
                            "【MOCHU-OA】" + reportTitle + " - " + LocalDate.now(),
                            reportContent);
                    }
                } catch (Exception e) {
                    log.warn("报表邮件推送失败: userId={}, error={}", sub.getUserId(), e.getMessage());
                }
            }
            log.info("{}推送完成: {}条", label, subs.size());
        } catch (Exception e) {
            log.error("{}推送异常", label, e);
        }
    }
}
