package com.mochu.system.init;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mochu.common.constant.Constants;
import com.mochu.system.entity.SysUser;
import com.mochu.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 应用启动初始化 — 确保 admin 账号可用
 * 检查 admin 密码是否为 admin123，不是则自动修正
 * 同时清除锁定状态和 Redis 登录失败计数
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_DEFAULT_PASSWORD = "admin123";

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
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

        // 检查密码是否匹配 admin123
        if (!passwordEncoder.matches(ADMIN_DEFAULT_PASSWORD, admin.getPasswordHash())) {
            log.warn("admin 密码哈希不匹配，自动重置为默认密码 admin123");
            admin.setPasswordHash(passwordEncoder.encode(ADMIN_DEFAULT_PASSWORD));
            needUpdate = true;
        }

        // 清除锁定状态
        if (admin.getLockUntil() != null || (admin.getLoginAttempts() != null && admin.getLoginAttempts() > 0)) {
            log.info("清除 admin 锁定状态和登录失败计数");
            admin.setLockUntil(null);
            admin.setLoginAttempts(0);
            needUpdate = true;
        }

        if (needUpdate) {
            sysUserMapper.updateById(admin);
            log.info("admin 账号已重置完成");
        }

        // 清除 Redis 登录失败计数
        String failKey = Constants.REDIS_LOGIN_FAIL_PREFIX + ADMIN_USERNAME;
        redisTemplate.delete(failKey);
    }
}
