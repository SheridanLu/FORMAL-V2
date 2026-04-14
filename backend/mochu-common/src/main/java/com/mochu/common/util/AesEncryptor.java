package com.mochu.common.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES-256-CBC 加解密工具
 * 存储格式: Base64(IV):Base64(密文)
 */
public class AesEncryptor {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int IV_LENGTH = 16;

    private AesEncryptor() {}

    /**
     * 加密
     * @param plaintext 明文
     * @param key 密钥（32字节 = 256位）
     * @return Base64(IV):Base64(密文)
     */
    public static String encrypt(String plaintext, String key) {
        if (plaintext == null || plaintext.isEmpty()) {
            return plaintext;
        }
        try {
            // #5 fix: 校验密钥长度，AES-256 要求 32 字节
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            if (keyBytes.length != 32) {
                throw new IllegalArgumentException(
                        "AES-256 key must be exactly 32 bytes, got " + keyBytes.length);
            }

            // 生成随机 IV
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // 密钥
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);

            // 加密
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // 拼接: Base64(IV):Base64(密文)
            return Base64.getEncoder().encodeToString(iv)
                    + ":"
                    + Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("AES加密失败", e);
        }
    }

    /**
     * 解密
     * @param ciphertext Base64(IV):Base64(密文)
     * @param key 密钥
     * @return 明文
     */
    public static String decrypt(String ciphertext, String key) {
        if (ciphertext == null || ciphertext.isEmpty()
                || !ciphertext.contains(":")) {
            return ciphertext;
        }
        try {
            String[] parts = ciphertext.split(":", 2);
            byte[] iv = Base64.getDecoder().decode(parts[0]);
            byte[] encrypted = Base64.getDecoder().decode(parts[1]);

            // #5 fix: 解密同样校验密钥长度
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            if (keyBytes.length != 32) {
                throw new IllegalArgumentException(
                        "AES-256 key must be exactly 32 bytes, got " + keyBytes.length);
            }

            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decrypted = cipher.doFinal(encrypted);

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES解密失败", e);
        }
    }
}
