package com.mochu.common.message.mock;

import com.mochu.common.message.WechatSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "message.wechat.enabled", havingValue = "false", matchIfMissing = true)
public class MockWechatSender implements WechatSender {
    @Override
    public void sendTextCard(String userId, String title, String description, String url) {
        log.info("[Mock微信] 向{}发送: title={}", userId, title);
    }
}
