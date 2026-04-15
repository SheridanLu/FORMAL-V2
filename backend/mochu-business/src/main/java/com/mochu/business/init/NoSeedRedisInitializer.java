package com.mochu.business.init;

import com.mochu.business.entity.BizNoSeed;
import com.mochu.business.mapper.BizNoSeedMapper;
import com.mochu.common.constant.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 应用启动时将 biz_no_seed 表中的当前序号同步到 Redis。
 *
 * <p>背景：NoGeneratorService 已从 DB SELECT FOR UPDATE 重构为 Redis INCR，
 * 但数据库中可能已有历史序号。首次部署时必须将 DB 序号作为 Redis 基线值，
 * 否则 Redis 从 0 开始递增会导致编号重复。
 *
 * <p>幂等：仅当 Redis 中尚不存在对应 Key 时才设置（SETNX 语义），
 * 避免服务重启覆盖已经通过 Redis INCR 推进的序号。
 */
@Slf4j
@Component
@Order(10) // 在 AdminInitializer 之后执行
@RequiredArgsConstructor
public class NoSeedRedisInitializer implements CommandLineRunner {

    private final BizNoSeedMapper noSeedMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void run(String... args) {
        List<BizNoSeed> seeds = noSeedMapper.selectAll();
        if (seeds == null || seeds.isEmpty()) {
            log.info("biz_no_seed 表为空，跳过 Redis 序号同步");
            return;
        }

        int synced = 0;
        for (BizNoSeed seed : seeds) {
            String key = Constants.REDIS_BIZ_NO_PREFIX + seed.getPrefix() + ":" + seed.getDatePart();
            // SETNX: 仅在 Key 不存在时设置，避免覆盖已有的 Redis 计数
            Boolean set = stringRedisTemplate.opsForValue()
                    .setIfAbsent(key, String.valueOf(seed.getCurrentSeq()));
            if (Boolean.TRUE.equals(set)) {
                log.info("同步编号种子到 Redis: {} = {}", key, seed.getCurrentSeq());
                synced++;
            }
        }
        log.info("编号种子 Redis 同步完成: 共 {} 条记录, 新同步 {} 条", seeds.size(), synced);
    }
}
