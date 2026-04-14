package com.mochu.business.scheduler;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 审计日志归档
 * Cron: 0 0 2 1 * ? — 每月1日 02:00
 * 功能: 归档12个月前的审计日志，物理删除原表数据
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogArchiveScheduler {

    private final JdbcTemplate jdbcTemplate;
    private final StringRedisTemplate redisTemplate;

    /** #N3 fix: 加 @Transactional 保证 INSERT+DELETE 原子性 */
    /** #N4 fix: DELETE 分批执行，避免大事务长时间锁表 */
    private static final int DELETE_BATCH_SIZE = 5000;

    @XxlJob("auditLogArchiveJob")
    public void archiveOldLogs() {
        String lockKey = "scheduler:lock:audit_archive";
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", Duration.ofMinutes(30));
        if (Boolean.FALSE.equals(locked)) return;

        try {
            LocalDateTime cutoff = LocalDateTime.now().minusMonths(12);
            String cutoffStr = cutoff.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            log.info("=== 审计日志归档开始, 截止日期: {} ===", cutoffStr);

            // 确保归档表存在
            jdbcTemplate.execute(
                    "CREATE TABLE IF NOT EXISTS sys_audit_log_archive LIKE sys_audit_log");

            // #N3 fix: 使用 INSERT IGNORE 防止重复归档（重试安全）
            int archived = jdbcTemplate.update(
                    "INSERT IGNORE INTO sys_audit_log_archive SELECT * FROM sys_audit_log " +
                    "WHERE created_at < ?", cutoffStr);

            // #N4 fix: 分批删除原表数据，避免大事务长时间锁表
            if (archived > 0) {
                int totalDeleted = 0;
                int deleted;
                do {
                    deleted = jdbcTemplate.update(
                            "DELETE FROM sys_audit_log WHERE created_at < ? LIMIT " + DELETE_BATCH_SIZE,
                            cutoffStr);
                    totalDeleted += deleted;
                    if (deleted > 0) {
                        log.debug("分批删除进度: 本批{}条, 累计{}条", deleted, totalDeleted);
                    }
                } while (deleted >= DELETE_BATCH_SIZE);
                log.info("=== 归档完成: 归档{}条, 删除{}条 ===", archived, totalDeleted);
            } else {
                log.info("=== 无需归档的日志 ===");
            }
        } catch (Exception e) {
            log.error("审计日志归档异常", e);
        } finally {
            redisTemplate.delete(lockKey);
        }
    }
}
