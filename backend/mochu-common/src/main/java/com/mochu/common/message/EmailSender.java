package com.mochu.common.message;

/**
 * 邮件发送接口
 */
public interface EmailSender {
    void send(String to, String subject, String content);
}
