package com.mochu.business.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mochu.business.entity.BizEquipment;
import com.mochu.business.mapper.BizEquipmentMapper;
import com.mochu.system.entity.SysTodo;
import com.mochu.system.mapper.SysTodoMapper;
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
 * 设备保养提醒
 * Cron: 0 40 0 * * ? — 每日 00:40
 * 功能: 扫描即将到保养日的设备(7/3/1天)，通知管理员
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EquipmentMaintenanceScheduler {

    private final BizEquipmentMapper equipmentMapper;
    private final SysTodoMapper todoMapper;
    private final StringRedisTemplate redisTemplate;

    @XxlJob("equipmentMaintenanceJob")
    public void checkMaintenanceSchedule() {
        String lockKey = "scheduler:lock:equipment_maintenance";
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", Duration.ofMinutes(10));
        if (Boolean.FALSE.equals(locked)) return;

        try {
            log.info("=== 设备保养提醒扫描开始 ===");
            LocalDate today = LocalDate.now();
            int warned = 0;

            List<BizEquipment> equipments = equipmentMapper.selectList(
                    new LambdaQueryWrapper<BizEquipment>()
                            .in(BizEquipment::getStatus, "idle", "in_use")
                            .isNotNull(BizEquipment::getNextMaintenanceDate)
                            .le(BizEquipment::getNextMaintenanceDate, today.plusDays(7))
                            .ge(BizEquipment::getNextMaintenanceDate, today));

            for (BizEquipment eq : equipments) {
                long days = ChronoUnit.DAYS.between(today, eq.getNextMaintenanceDate());
                if (days != 7 && days != 3 && days != 1 && days != 0) continue;

                String dedupKey = "equipment_maint:warned:" + eq.getId() + ":" + days;
                if (Boolean.TRUE.equals(redisTemplate.hasKey(dedupKey))) continue;

                // 通知管理员(userId=1)
                SysTodo todo = new SysTodo();
                todo.setUserId(1);
                todo.setTitle(String.format("【保养提醒】设备\"%s\"(%s)%s需要保养",
                        eq.getEquipmentName(), eq.getEquipmentNo(),
                        days == 0 ? "今天" : "还剩" + days + "天"));
                todo.setContent(String.format("设备位置: %s，下次保养日期: %s",
                        eq.getLocation(), eq.getNextMaintenanceDate()));
                todo.setBizType("equipment_maintenance");
                todo.setBizId(eq.getId());
                todo.setStatus(0);
                todoMapper.insert(todo);

                redisTemplate.opsForValue().set(dedupKey, "1", Duration.ofHours(24));
                warned++;
            }
            log.info("=== 设备保养提醒完成: 提醒{}条 ===", warned);
        } catch (Exception e) {
            log.error("设备保养提醒异常", e);
        } finally {
            redisTemplate.delete(lockKey);
        }
    }
}
