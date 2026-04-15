package com.mochu.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

/**
 * 用户通知偏好
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_notification_config")
public class SysUserNotificationConfig extends BaseEntity {

    private Integer userId;
    /** 站内信(始终开启) */
    private Boolean channelStation;
    /** 短信通知 */
    private Boolean channelSms;
    /** 邮件通知 */
    private Boolean channelEmail;
    /** 企业微信通知 */
    private Boolean channelWechat;
    /** 免打扰开始时间 */
    private LocalTime quietStart;
    /** 免打扰结束时间 */
    private LocalTime quietEnd;
}
