package com.mochu.framework.handler;

import com.mochu.common.util.AesEncryptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 加密字段的 MyBatis TypeHandler
 * 写入时自动加密，读取时自动解密
 */
@Slf4j
@Component
public class EncryptedStringTypeHandler extends BaseTypeHandler<String> {

    private static String encryptionKey;

    @Value("${encryption.aes-key}")
    public void setEncryptionKey(String key) {
        EncryptedStringTypeHandler.encryptionKey = key;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
                                     String parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, AesEncryptor.encrypt(parameter, encryptionKey));
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        String value = rs.getString(columnName);
        return value != null ? AesEncryptor.decrypt(value, encryptionKey) : null;
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        String value = rs.getString(columnIndex);
        return value != null ? AesEncryptor.decrypt(value, encryptionKey) : null;
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        String value = cs.getString(columnIndex);
        return value != null ? AesEncryptor.decrypt(value, encryptionKey) : null;
    }
}
