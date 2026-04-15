package com.mochu.business.init;

import com.mochu.common.util.AesEncryptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 存量敏感数据加密迁移 — 一次性执行。
 *
 * <p>背景：BizSupplier(contactPhone/bankAccount/taxNo) 和 BizHrEntry(phone/idCardNo)
 * 添加了 EncryptedStringTypeHandler，但数据库中已有的明文数据需要加密处理。
 *
 * <p>判断逻辑：AES-256-CBC 加密后的格式为 "Base64(IV):Base64(密文)"，包含冒号。
 * 明文数据不包含冒号（手机号、身份证号、银行账号、税号都不含冒号），
 * 因此通过检查是否包含冒号来区分已加密和未加密的数据。
 *
 * <p>幂等：每条记录只会被加密一次（已加密的数据含冒号，跳过）。
 * 服务多次重启不会重复加密。
 */
@Slf4j
@Component
@Order(20) // 在 NoSeedRedisInitializer 之后执行
@RequiredArgsConstructor
public class SensitiveDataEncryptionMigrator implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Value("${encryption.aes-key}")
    private String aesKey;

    @Override
    public void run(String... args) {
        log.info("开始检查存量敏感数据加密状态...");

        int total = 0;
        total += migrateTable("biz_supplier", "id",
                new String[]{"contact_phone", "bank_account", "tax_no"});
        total += migrateTable("biz_hr_entry", "id",
                new String[]{"phone", "id_card_no"});

        if (total > 0) {
            log.info("存量敏感数据加密迁移完成，共加密 {} 个字段值", total);
        } else {
            log.info("所有敏感数据均已加密，无需迁移");
        }
    }

    /**
     * 迁移指定表的指定列
     *
     * @return 加密的字段值数量
     */
    private int migrateTable(String table, String pkColumn, String[] columns) {
        // 构建查询：只选取存在至少一个非空、未加密字段的行
        StringBuilder whereClauses = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) whereClauses.append(" OR ");
            // 未加密 = 非空且不含冒号
            whereClauses.append(columns[i]).append(" IS NOT NULL AND ")
                    .append(columns[i]).append(" != '' AND ")
                    .append(columns[i]).append(" NOT LIKE '%:%'");
        }

        String sql = String.format("SELECT %s, %s FROM %s WHERE %s",
                pkColumn, String.join(", ", columns), table, whereClauses);

        List<Map<String, Object>> rows;
        try {
            rows = jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            log.warn("查询表 {} 失败（可能表不存在），跳过: {}", table, e.getMessage());
            return 0;
        }

        if (rows.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (Map<String, Object> row : rows) {
            Object pk = row.get(pkColumn);
            for (String col : columns) {
                Object value = row.get(col);
                if (value == null) continue;
                String plaintext = value.toString();
                // 跳过空字符串和已加密数据（含冒号）
                if (plaintext.isEmpty() || plaintext.contains(":")) continue;

                String encrypted = AesEncryptor.encrypt(plaintext, aesKey);
                String updateSql = String.format("UPDATE %s SET %s = ? WHERE %s = ?",
                        table, col, pkColumn);
                jdbcTemplate.update(updateSql, encrypted, pk);
                count++;
            }
        }

        if (count > 0) {
            log.info("表 {} 加密了 {} 个字段值", table, count);
        }
        return count;
    }
}
