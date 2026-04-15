package com.mochu.system.init;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mochu.common.constant.Constants;
import com.mochu.system.entity.SysUser;
import com.mochu.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 应用启动初始化 — 确保 admin 账号可用
 * 仅在 admin 账号被锁定时自动清除锁定状态（安全恢复机制）。
 * 不再自动重置密码 — 生产环境不应每次启动覆盖管理员密码。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private static final String ADMIN_USERNAME = "admin";

    private final SysUserMapper sysUserMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void run(String... args) {
        SysUser admin = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, ADMIN_USERNAME)
        );
        if (admin == null) {
            log.warn("admin 账号不存在，跳过初始化");
            return;
        }

        boolean needUpdate = false;

        // 仅清除锁定状态（安全恢复机制，防止管理员被永久锁定）
        if (admin.getLockUntil() != null || (admin.getLoginAttempts() != null && admin.getLoginAttempts() > 0)) {
            log.info("清除 admin 锁定状态和登录失败计数");
            admin.setLockUntil(null);
            admin.setLoginAttempts(0);
            needUpdate = true;
        }

        if (needUpdate) {
            sysUserMapper.updateById(admin);
            log.info("admin 账号锁定状态已清除");
        }

        // 清除 Redis 登录失败计数
        String failKey = Constants.REDIS_LOGIN_FAIL_PREFIX + ADMIN_USERNAME;
        redisTemplate.delete(failKey);
    }
}
