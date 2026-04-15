package com.mochu.system.service.message;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mochu.common.message.EmailSender;
import com.mochu.common.message.SmsSender;
import com.mochu.common.message.WechatSender;
import com.mochu.system.entity.SysTodo;
import com.mochu.system.entity.SysUser;
import com.mochu.system.entity.SysUserNotificationConfig;
import com.mochu.system.mapper.SysTodoMapper;
import com.mochu.system.mapper.SysUserMapper;
import com.mochu.system.mapper.SysUserNotificationConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SysTodoMapper todoMapper;
    private final SysUserMapper userMapper;
    private final SysUserNotificationConfigMapper configMapper;
    private final SmsSender smsSender;
    private final EmailSender emailSender;
    private final WechatSender wechatSender;

    /**
     * 统一通知 — 站内信(必发) + 根据用户偏好发送其他渠道
     */
    public void notify(Integer userId, String title, String content,
                       String bizType, Integer bizId) {
        // 1. 站内信（必发）
        SysTodo todo = new SysTodo();
        todo.setUserId(userId);
        todo.setTitle(title);
        todo.setContent(content);
        todo.setBizType(bizType);
        todo.setBizId(bizId);
        todo.setStatus(0);
        todoMapper.insert(todo);

        // 2. 查询用户通知偏好
        SysUserNotificationConfig config = configMapper.selectOne(
                new LambdaQueryWrapper<SysUserNotificationConfig>()
                        .eq(SysUserNotificationConfig::getUserId, userId));

        if (config == null) return; // 未配置则仅站内信

        // 3. 免打扰时段检查
        if (isQuietTime(config)) {
            log.debug("用户{}处于免打扰时段，跳过外部渠道通知", userId);
            return;
        }

        // 4. 获取用户联系方式
        SysUser user = userMapper.selectById(userId);
        if (user == null) return;

        // 5. 短信通知
        if (Boolean.TRUE.equals(config.getChannelSms()) && StringUtils.hasText(user.getPhone())) {
            try {
                smsSender.sendNotify(user.getPhone(), "notification", Map.of("title", title));
            } catch (Exception e) {
                log.warn("短信通知发送失败: userId={}", userId, e);
            }
        }

        // 6. 邮件通知
        if (Boolean.TRUE.equals(config.getChannelEmail()) && StringUtils.hasText(user.getEmail())) {
            try {
                emailSender.send(user.getEmail(), title, content);
            } catch (Exception e) {
                log.warn("邮件通知发送失败: userId={}", userId, e);
            }
        }

        // 7. 企业微信通知
        if (Boolean.TRUE.equals(config.getChannelWechat()) && StringUtils.hasText(user.getWxUserid())) {
            try {
                String url = ""; // 可以根据 bizType+bizId 构建前端路由
                wechatSender.sendTextCard(user.getWxUserid(), title, content, url);
            } catch (Exception e) {
                log.warn("企业微信通知发送失败: userId={}", userId, e);
            }
        }
    }

    /**
     * 检查是否处于免打扰时段
     */
    private boolean isQuietTime(SysUserNotificationConfig config) {
        if (config.getQuietStart() == null || config.getQuietEnd() == null) return false;
        LocalTime now = LocalTime.now();
        LocalTime start = config.getQuietStart();
        LocalTime end = config.getQuietEnd();
        if (start.isBefore(end)) {
            // 正常时段: 22:00 ~ 08:00 → start=22:00, end=08:00 → 跨天
            return !now.isBefore(start) && now.isBefore(end);
        } else {
            // 跨天时段
            return !now.isBefore(start) || now.isBefore(end);
        }
    }
}
