package com.mochu.common.message;

/**
 * 短信发送接口
 */
public interface SmsSender {
    void sendCode(String phone, String code);
    void sendNotify(String phone, String template, java.util.Map<String, String> params);
}
