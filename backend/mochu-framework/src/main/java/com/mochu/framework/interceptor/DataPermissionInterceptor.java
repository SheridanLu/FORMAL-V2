package com.mochu.framework.interceptor;

import com.mochu.common.security.LoginUser;
import com.mochu.common.security.SecurityUtils;
import com.mochu.framework.annotation.DataScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * MyBatis 数据权限拦截器
 * 根据当前用户 dataScope 自动在 SQL 中拼接 WHERE 条件
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Intercepts({
    @Signature(type = Executor.class, method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class DataPermissionInterceptor implements Interceptor {

    private final StringRedisTemplate redisTemplate;

    /** 别名白名单: 仅允许字母/下划线/数字，防止 SQL 注入 */
    private static final Pattern SAFE_ALIAS = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]{0,30}$");

    private void validateAlias(String alias) {
        if (alias != null && !alias.isEmpty() && !SAFE_ALIAS.matcher(alias).matches()) {
            throw new SecurityException("Invalid DataScope alias: " + alias);
        }
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];

        // 查找 @DataScope 注解
        DataScope dataScope = findDataScopeAnnotation(ms);
        if (dataScope == null) {
            return invocation.proceed();
        }

        // 获取当前用户
        LoginUser loginUser;
        try {
            loginUser = SecurityUtils.getCurrentUser();
        } catch (Exception e) {
            // 未登录场景直接放行（公开接口）
            return invocation.proceed();
        }

        if (loginUser == null) {
            return invocation.proceed();
        }

        // 根据 dataScope 构建条件
        String sqlCondition = buildSqlCondition(loginUser, dataScope);
        if (sqlCondition == null || sqlCondition.isEmpty()) {
            // dataScope=1 全部数据，不拼接
            return invocation.proceed();
        }

        // 修改 SQL
        BoundSql boundSql = ms.getBoundSql(parameter);
        String originalSql = boundSql.getSql();
        String newSql = injectCondition(originalSql, sqlCondition);

        // 替换 MappedStatement
        BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), newSql,
                boundSql.getParameterMappings(), parameter);
        MappedStatement newMs = copyMappedStatement(ms, () -> newBoundSql);
        invocation.getArgs()[0] = newMs;

        return invocation.proceed();
    }

    /**
     * 根据用户数据范围构建 SQL 条件
     */
    private String buildSqlCondition(LoginUser loginUser, DataScope ds) {
        // #1 fix: 校验别名格式，防止 SQL 注入
        validateAlias(ds.deptAlias());
        validateAlias(ds.userAlias());
        validateAlias(ds.projectAlias());

        Integer scope = loginUser.getDataScope();
        if (scope == null || scope == 1) {
            // 全部数据，不拼接
            return null;
        }

        StringBuilder condition = new StringBuilder();

        switch (scope) {
            case 2: // 本部门
                if (!ds.deptAlias().isEmpty()) {
                    condition.append(String.format(" AND %s.dept_id = %d",
                            ds.deptAlias(), loginUser.getDeptId()));
                } else {
                    condition.append(String.format(" AND dept_id = %d", loginUser.getDeptId()));
                }
                break;

            case 3: // 本项目
                List<Integer> projectIds = getUserProjectIds(loginUser.getUserId());
                if (projectIds.isEmpty()) {
                    // 无项目权限，返回空结果条件
                    condition.append(" AND 1 = 0");
                } else {
                    String ids = projectIds.stream().map(String::valueOf)
                            .collect(Collectors.joining(","));
                    if (!ds.projectAlias().isEmpty()) {
                        condition.append(String.format(" AND %s.project_id IN (%s)",
                                ds.projectAlias(), ids));
                    } else {
                        condition.append(String.format(" AND project_id IN (%s)", ids));
                    }
                }
                break;

            case 4: // 仅本人
                if (!ds.userAlias().isEmpty()) {
                    condition.append(String.format(" AND %s.creator_id = %d",
                            ds.userAlias(), loginUser.getUserId()));
                } else {
                    condition.append(String.format(" AND creator_id = %d",
                            loginUser.getUserId()));
                }
                break;

            case 5: // 自定义 — 通过 sys_role_data_scope 关联的部门
                List<Integer> deptIds = getCustomDeptIds(loginUser.getUserId());
                if (deptIds.isEmpty()) {
                    condition.append(" AND 1 = 0");
                } else {
                    String dIds = deptIds.stream().map(String::valueOf)
                            .collect(Collectors.joining(","));
                    if (!ds.deptAlias().isEmpty()) {
                        condition.append(String.format(" AND %s.dept_id IN (%s)",
                                ds.deptAlias(), dIds));
                    } else {
                        condition.append(String.format(" AND dept_id IN (%s)", dIds));
                    }
                }
                break;

            default:
                break;
        }

        return condition.toString();
    }

    /**
     * 查询用户关联的项目ID（通过 biz_project_member）
     */
    private List<Integer> getUserProjectIds(Integer userId) {
        // 优先从 Redis 缓存获取
        String cacheKey = "user:projects:" + userId;
        Set<String> cached = redisTemplate.opsForSet().members(cacheKey);
        if (cached != null && !cached.isEmpty()) {
            return cached.stream().map(Integer::parseInt).collect(Collectors.toList());
        }
        // 缓存未命中时返回空（需在用户登录时预热缓存）
        return Collections.emptyList();
    }

    /**
     * 查询自定义数据范围的部门ID（通过 sys_role_data_scope）
     */
    private List<Integer> getCustomDeptIds(Integer userId) {
        String cacheKey = "user:data_scope_depts:" + userId;
        Set<String> cached = redisTemplate.opsForSet().members(cacheKey);
        if (cached != null && !cached.isEmpty()) {
            return cached.stream().map(Integer::parseInt).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 在 SQL 的 WHERE 子句后注入条件
     */
    private String injectCondition(String sql, String condition) {
        String upperSql = sql.toUpperCase();
        int whereIndex = upperSql.lastIndexOf("WHERE");
        if (whereIndex > 0) {
            // 已有 WHERE，在 WHERE 后附加
            int orderByIndex = upperSql.indexOf("ORDER BY", whereIndex);
            int limitIndex = upperSql.indexOf("LIMIT", whereIndex);
            int groupByIndex = upperSql.indexOf("GROUP BY", whereIndex);

            int insertPos = sql.length();
            if (orderByIndex > 0) insertPos = Math.min(insertPos, orderByIndex);
            if (limitIndex > 0) insertPos = Math.min(insertPos, limitIndex);
            if (groupByIndex > 0) insertPos = Math.min(insertPos, groupByIndex);

            return sql.substring(0, insertPos) + condition + " " + sql.substring(insertPos);
        } else {
            // 无 WHERE，在 FROM ... 后添加 WHERE 1=1 + 条件
            return sql + " WHERE 1=1 " + condition;
        }
    }

    /**
     * 从 MappedStatement 中查找 @DataScope 注解
     */
    private DataScope findDataScopeAnnotation(MappedStatement ms) {
        try {
            String id = ms.getId();
            int lastDot = id.lastIndexOf('.');
            String className = id.substring(0, lastDot);
            String methodName = id.substring(lastDot + 1);

            Class<?> clazz = Class.forName(className);
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(methodName)) {
                    DataScope annotation = method.getAnnotation(DataScope.class);
                    if (annotation != null) return annotation;
                }
            }
            // 检查类级别注解
            return clazz.getAnnotation(DataScope.class);
        } catch (Exception e) {
            return null;
        }
    }

    private MappedStatement copyMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(
                ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.fetchSize(ms.getFetchSize());
        builder.timeout(ms.getTimeout());
        builder.statementType(ms.getStatementType());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        builder.keyGenerator(ms.getKeyGenerator());
        builder.keyProperty(ms.getKeyProperties() != null
                ? String.join(",", ms.getKeyProperties()) : null);
        builder.keyColumn(ms.getKeyColumns() != null
                ? String.join(",", ms.getKeyColumns()) : null);
        builder.databaseId(ms.getDatabaseId());
        builder.lang(ms.getLang());
        builder.resultOrdered(ms.isResultOrdered());
        return builder.build();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
}
