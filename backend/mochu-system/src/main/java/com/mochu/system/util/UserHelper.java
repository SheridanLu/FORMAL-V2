package com.mochu.system.util;

import com.mochu.system.entity.SysUser;
import com.mochu.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 用户名解析工具 — 消除各 Scheduler/Service 中重复的 resolveUserName 私有方法
 */
@Component
@RequiredArgsConstructor
public class UserHelper {

    private final SysUserMapper userMapper;

    /**
     * 通过 userId 解析用户真实姓名
     *
     * @param userId 用户ID
     * @return 真实姓名；null 时返回 "未知"，查无此人时返回 "用户{id}"
     */
    public String resolveRealName(Integer userId) {
        if (userId == null) return "未知";
        SysUser user = userMapper.selectById(userId);
        return user != null ? user.getRealName() : "用户" + userId;
    }

    /**
     * 通过 userId 解析用户真实姓名（空串兜底版，适用于 UI 展示）
     */
    public String resolveRealNameOrEmpty(Integer userId) {
        if (userId == null) return "";
        SysUser user = userMapper.selectById(userId);
        return user != null ? user.getRealName() : "";
    }
}
