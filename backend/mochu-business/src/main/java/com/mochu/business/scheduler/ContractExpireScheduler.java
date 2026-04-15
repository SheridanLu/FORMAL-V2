package com.mochu.business.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mochu.business.entity.BizContract;
import com.mochu.business.entity.BizProjectMember;
import com.mochu.business.mapper.BizContractMapper;
import com.mochu.business.mapper.BizProjectMemberMapper;
import com.mochu.system.service.message.NotificationService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 合同到期预警
 * Cron: 0 0 1 * * ? — 每日 01:00
 * 功能: 扫描即将到期的业务合同(30/15/7天)，通知项目经理
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ContractExpireScheduler {

    private final BizContractMapper contractMapper;
    private final BizProjectMemberMapper memberMapper;
    private final NotificationService notificationService;
    private final StringRedisTemplate redisTemplate;

    @XxlJob("contractExpireJob")
    public void checkContractExpiry() {
        String lockKey = "scheduler:lock:contract_expire";
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", Duration.ofMinutes(10));
        if (Boolean.FALSE.equals(locked)) return;

        try {
            log.info("=== 合同到期预警扫描开始 ===");
            LocalDate today = LocalDate.now();
            int warned = 0;

            // 查询 endDate 在今天~30天内的有效合同
            List<BizContract> contracts = contractMapper.selectList(
                    new LambdaQueryWrapper<BizContract>()
                            .in(BizContract::getStatus, "approved", "completed")
                            .le(BizContract::getEndDate, today.plusDays(30))
                            .ge(BizContract::getEndDate, today));

            for (BizContract c : contracts) {
                long days = ChronoUnit.DAYS.between(today, c.getEndDate());
                if (days != 30 && days != 15 && days != 7 && days != 3 && days != 1) continue;

                // 24h去重
                String dedupKey = "contract_expire:warned:" + c.getId() + ":" + days;
                if (Boolean.TRUE.equals(redisTemplate.hasKey(dedupKey))) continue;

                // 通知项目经理
                Integer managerId = findProjectManager(c.getProjectId());
                if (managerId == null) continue;

                String title = String.format("【合同到期】合同\"%s\"还剩%d天到期",
                        c.getContractName(), days);
                String content = String.format("合同编号: %s，结束日期: %s",
                        c.getContractNo(), c.getEndDate());

                notificationService.notify(managerId, title, content,
                        "contract_expire", c.getId());

                redisTemplate.opsForValue().set(dedupKey, "1", Duration.ofHours(24));
                warned++;
            }
            log.info("=== 合同到期预警完成: 预警{}条 ===", warned);
        } catch (Exception e) {
            log.error("合同到期预警异常", e);
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    private Integer findProjectManager(Integer projectId) {
        if (projectId == null) return null;
        BizProjectMember member = memberMapper.selectOne(
                new LambdaQueryWrapper<BizProjectMember>()
                        .eq(BizProjectMember::getProjectId, projectId)
                        .eq(BizProjectMember::getRole, "manager")
                        .last("LIMIT 1"));
        return member != null ? member.getUserId() : null;
    }
}
