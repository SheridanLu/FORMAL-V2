package com.mochu.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "encryption")
public class EncryptionConfig {

    /** AES-256 密钥（必须 32 字符 = 256 位） */
    private String aesKey;
}
