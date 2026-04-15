package com.mochu.business.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mochu.business.entity.BizReceiptPlan;
import com.mochu.system.entity.SysTodo;
import com.mochu.system.mapper.SysTodoMapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.mochu.business.mapper.BizReceiptPlanMapper;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 回款截止提醒
 * Cron: 0 20 0 * * ? — 每日 00:20
 * 功能: 扫描即将到期的收款计划(7/3/1天)，通知相关人员
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentDeadlineScheduler {

    private final BizReceiptPlanMapper receiptPlanMapper;
    private final SysTodoMapper todoMapper;
    private final StringRedisTemplate redisTemplate;

    @XxlJob("paymentDeadlineJob")
    public void checkPaymentDeadlines() {
        String lockKey = "scheduler:lock:payment_deadline";
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", Duration.ofMinutes(10));
        if (Boolean.FALSE.equals(locked)) return;

        try {
            log.info("=== 回款截止提醒扫描开始 ===");
            LocalDate today = LocalDate.now();
            int warned = 0;

            List<BizReceiptPlan> plans = receiptPlanMapper.selectList(
                    new LambdaQueryWrapper<BizReceiptPlan>()
                            .eq(BizReceiptPlan::getStatus, "pending")
                            .le(BizReceiptPlan::getPlanDate, today.plusDays(7))
                            .ge(BizReceiptPlan::getPlanDate, today));

            for (BizReceiptPlan plan : plans) {
                long days = ChronoUnit.DAYS.between(today, plan.getPlanDate());
                if (days != 7 && days != 3 && days != 1 && days != 0) continue;

                String dedupKey = "payment_deadline:warned:" + plan.getId() + ":" + days;
                if (Boolean.TRUE.equals(redisTemplate.hasKey(dedupKey))) continue;

                // 通知创建者
                SysTodo todo = new SysTodo();
                todo.setUserId(plan.getCreatorId());
                todo.setTitle(String.format("【回款提醒】第%d期收款计划%s到期",
                        plan.getPlanNo(), days == 0 ? "今天" : "还剩" + days + "天"));
                todo.setContent(String.format("计划金额: %s，计划日期: %s",
                        plan.getPlanAmount(), plan.getPlanDate()));
                todo.setBizType("payment_deadline");
                todo.setBizId(plan.getId());
                todo.setStatus(0);
                todoMapper.insert(todo);

                redisTemplate.opsForValue().set(dedupKey, "1", Duration.ofHours(24));
                warned++;
            }
            log.info("=== 回款截止提醒完成: 提醒{}条 ===", warned);
        } catch (Exception e) {
            log.error("回款截止提醒异常", e);
        } finally {
            redisTemplate.delete(lockKey);
        }
    }
}
