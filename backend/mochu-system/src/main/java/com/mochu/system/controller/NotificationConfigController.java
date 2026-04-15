package com.mochu.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mochu.common.result.R;
import com.mochu.common.security.SecurityUtils;
import com.mochu.framework.annotation.AuditLog;
import com.mochu.system.entity.SysUserNotificationConfig;
import com.mochu.system.mapper.SysUserNotificationConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 用户通知偏好 Controller
 */
@RestController
@RequestMapping("/api/v1/user/notification-config")
@RequiredArgsConstructor
public class NotificationConfigController {

    private final SysUserNotificationConfigMapper configMapper;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public R<SysUserNotificationConfig> getMyConfig() {
        Integer userId = SecurityUtils.getCurrentUserId();
        SysUserNotificationConfig config = configMapper.selectOne(
                new LambdaQueryWrapper<SysUserNotificationConfig>()
                        .eq(SysUserNotificationConfig::getUserId, userId));
        if (config == null) {
            config = new SysUserNotificationConfig();
            config.setUserId(userId);
            config.setChannelStation(true);
            config.setChannelSms(false);
            config.setChannelEmail(false);
            config.setChannelWechat(false);
        }
        return R.ok(config);
    }

    @PutMapping
    @AuditLog(operateType = "UPDATE", operateModule = "通知配置", bizType = "notification_config", saveBefore = true)
    @PreAuthorize("isAuthenticated()")
    public R<Void> updateMyConfig(@RequestBody SysUserNotificationConfig config) {
        Integer userId = SecurityUtils.getCurrentUserId();
        SysUserNotificationConfig existing = configMapper.selectOne(
                new LambdaQueryWrapper<SysUserNotificationConfig>()
                        .eq(SysUserNotificationConfig::getUserId, userId));
        config.setUserId(userId);
        config.setChannelStation(true); // 站内信始终开启
        if (existing == null) {
            configMapper.insert(config);
        } else {
            config.setId(existing.getId());
            configMapper.updateById(config);
        }
        return R.ok();
    }
}
