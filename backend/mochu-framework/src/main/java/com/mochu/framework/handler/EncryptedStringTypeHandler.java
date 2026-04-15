package com.mochu.framework.handler;

import com.mochu.common.util.AesEncryptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 加密字段的 MyBatis TypeHandler
 * 写入时自动加密，读取时自动解密。
 *
 * <p><b>重要：不要标注 @Component！</b>
 * Spring Boot / MyBatis-Plus 自动配置会扫描容器中的 TypeHandler Bean
 * 并将其注册为全局 TypeHandler。由于本类继承 BaseTypeHandler&lt;String&gt;，
 * 若注册为 Bean 则会覆盖默认 String 处理器，导致所有 String 字段的
 * 读写都经过 AES 加密/解密，破坏正常业务逻辑。
 *
 * <p>仅在需要加密的字段上通过
 * {@code @TableField(typeHandler = EncryptedStringTypeHandler.class)}
 * 显式指定即可生效。密钥通过 {@link EncryptedStringTypeHandlerInitializer} 注入。
 */
@Slf4j
public class EncryptedStringTypeHandler extends BaseTypeHandler<String> {

    private static String encryptionKey;

    /** 由 EncryptedStringTypeHandlerInitializer 在应用启动时调用 */
    public static void setEncryptionKey(String key) {
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
