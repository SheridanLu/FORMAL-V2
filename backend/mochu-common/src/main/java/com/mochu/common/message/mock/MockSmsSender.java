package com.mochu.common.message.mock;

import com.mochu.common.message.SmsSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(name = "message.sms.provider", havingValue = "mock", matchIfMissing = true)
public class MockSmsSender implements SmsSender {
    @Override
    public void sendCode(String phone, String code) {
        log.info("[Mock短信] 向{}发送验证码: {}", phone, code);
    }
    @Override
    public void sendNotify(String phone, String template, Map<String, String> params) {
        log.info("[Mock短信] 向{}发送通知: template={}, params={}", phone, template, params);
    }
}
