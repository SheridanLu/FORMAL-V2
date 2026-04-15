package com.mochu.framework.handler;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 在应用启动时将加密密钥注入到 EncryptedStringTypeHandler 的静态字段中。
 *
 * <p>EncryptedStringTypeHandler 不能标注 @Component（否则会被 MyBatis 自动注册
 * 为全局 String TypeHandler），所以通过这个独立的初始化器来注入配置。
 */
@Component
public class EncryptedStringTypeHandlerInitializer {

    @Value("${encryption.aes-key}")
    private String aesKey;

    @PostConstruct
    public void init() {
        EncryptedStringTypeHandler.setEncryptionKey(aesKey);
    }
}
