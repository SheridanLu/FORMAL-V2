package com.mochu.business.service;

import com.mochu.common.constant.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 编号生成服务 — Redis INCR 原子递增实现
 * 替代原 DB SELECT FOR UPDATE 方案，消除行锁瓶颈，支持高并发
 * 生成格式: 前缀 + 日期部分 + 序号（如 PJ260407001）
 * Redis Key: biz_no:{前缀}:{日期部分}
 */
@Service
@RequiredArgsConstructor
public class NoGeneratorService {

    private final StringRedisTemplate stringRedisTemplate;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyMMdd");
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyMM");

    /**
     * 生成编号
     * @param prefix 前缀（如 PJ/CT/PO 等）
     * @param seqWidth 序号位数（如 3 → 001）
     */
    public String generate(String prefix, int seqWidth) {
        String datePart = LocalDate.now().format(DATE_FMT);
        String key = Constants.REDIS_BIZ_NO_PREFIX + prefix + ":" + datePart;
        long seq = stringRedisTemplate.opsForValue().increment(key);
        // 首次创建时设置过期时间（2天，覆盖当天 + 跨日缓冲）
        if (seq == 1) {
            stringRedisTemplate.expire(key, Duration.ofDays(2));
        }
        String seqStr = String.format("%0" + seqWidth + "d", seq);
        return prefix + datePart + seqStr;
    }

    /**
     * 生成编号（默认3位序号）
     */
    public String generate(String prefix) {
        return generate(prefix, 3);
    }

    /**
     * P6 §4.4: 虚拟项目编号 — 按月重置
     * 格式: V+YYMM+3位顺序号，每月重置
     * 例: V2604001
     */
    public String generateMonthly(String prefix, int seqWidth) {
        String datePart = LocalDate.now().format(MONTH_FMT);
        String key = Constants.REDIS_BIZ_NO_PREFIX + prefix + ":" + datePart;
        long seq = stringRedisTemplate.opsForValue().increment(key);
        if (seq == 1) {
            stringRedisTemplate.expire(key, Duration.ofDays(35));
        }
        String seqStr = String.format("%0" + seqWidth + "d", seq);
        return prefix + datePart + seqStr;
    }

    /**
     * P6 §4.8: 全局递增编号 — 不按日期重置
     * 用于材料编码: M000001, M000002, ...
     */
    public String generateGlobal(String prefix, int seqWidth) {
        String key = Constants.REDIS_BIZ_NO_PREFIX + prefix + ":GLOBAL";
        long seq = stringRedisTemplate.opsForValue().increment(key);
        // 全局编号永不过期
        String seqStr = String.format("%0" + seqWidth + "d", seq);
        return prefix + seqStr;
    }
}
