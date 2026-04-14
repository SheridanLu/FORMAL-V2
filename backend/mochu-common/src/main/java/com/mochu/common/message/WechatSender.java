package com.mochu.common.message;

/**
 * 企业微信发送接口
 */
public interface WechatSender {
    void sendTextCard(String userId, String title, String description, String url);
}
