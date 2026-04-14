package com.mochu.common.message.mock;

import com.mochu.common.message.EmailSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "message.email.provider", havingValue = "mock", matchIfMissing = true)
public class MockEmailSender implements EmailSender {
    @Override
    public void send(String to, String subject, String content) {
        log.info("[Mock邮件] 向{}发送: subject={}", to, subject);
    }
}
