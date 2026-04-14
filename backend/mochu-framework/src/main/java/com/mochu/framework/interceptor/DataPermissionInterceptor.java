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
 * MyBatis 数据权限拦截器 (V3.2)
 * 根据当前用户 dataScope 自动在 SQL 中拼接 WHERE 条件
 *
 * <p>dataScope 值含义 (V3.2 规范):
 * <ul>
 *   <li>1 = 全部数据 — 不拼接任何条件</li>
 *   <li>2 = 本部门及子部门 — 递归包含所有下级部门 (MySQL 8 CTE)</li>
 *   <li>3 = 仅本部门 — dept_id = 当前用户部门</li>
 *   <li>4 = 仅本人 — creator_id = 当前用户</li>
 *   <li>5 = 自定义 — 通过 sys_role_data_scope 关联的部门列表</li>
 * </ul>
 *
 * <p>项目过滤: 当 {@code @DataScope} 注解设置了 projectAlias 时,
 * 无论 dataScope 值如何, 都会额外附加项目成员过滤条件 (正交于部门/用户维度).
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
            // 无需拼接条件（全部数据 且 无项目过滤）
            return invocation.proceed();
        }

        // 修改 SQL
        BoundSql boundSql = ms.getBoundSql(parameter);
        String originalSql = boundSql.getSql();
        String newSql = injectCondition(originalSql, sqlCondition);

        // 替换 MappedStatement
        BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), newSql,
                boundSql.getParameterMappings(), parameter);
        MappedStatement newMs = copyMappedStatement(ms, paramObj -> newBoundSql);
        invocation.getArgs()[0] = newMs;

        return invocation.proceed();
    }

    /**
     * 根据用户数据范围构建 SQL 条件 (V3.2)
     *
     * <p>部门/用户维度 (由 dataScope 控制):
     * 1=全部数据, 2=本部门及子部门, 3=仅本部门, 4=仅本人, 5=自定义
     *
     * <p>项目维度 (正交): 当 projectAlias 非空时始终附加项目成员过滤
     */
    private String buildSqlCondition(LoginUser loginUser, DataScope ds) {
        // #1 fix: 校验别名格式，防止 SQL 注入
        validateAlias(ds.deptAlias());
        validateAlias(ds.userAlias());
        validateAlias(ds.projectAlias());

        Integer scope = loginUser.getDataScope();
        StringBuilder condition = new StringBuilder();

        // ── 部门/用户维度过滤 ──
        if (scope != null && scope != 1) {
            String deptAlias = ds.deptAlias();
            String deptCol = deptAlias.isEmpty() ? "dept_id" : deptAlias + ".dept_id";

            switch (scope) {
                case 2: // 本部门及子部门 — MySQL 8 递归 CTE
                    long deptId = loginUser.getDeptId();
                    condition.append(String.format(
                            " AND %s IN (WITH RECURSIVE dept_tree AS (" +
                            "SELECT id FROM sys_dept WHERE id = %d " +
                            "UNION ALL " +
                            "SELECT d.id FROM sys_dept d JOIN dept_tree dt ON d.parent_id = dt.id" +
                            ") SELECT id FROM dept_tree)",
                            deptCol, deptId));
                    break;

                case 3: // 仅本部门
                    condition.append(String.format(" AND %s = %d",
                            deptCol, loginUser.getDeptId()));
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
                        condition.append(String.format(" AND %s IN (%s)", deptCol, dIds));
                    }
                    break;

                default:
                    break;
            }
        }

        // ── 项目维度过滤 (正交于 dataScope) ──
        // 当 @DataScope 注解设置了 projectAlias，始终附加项目成员条件
        if (!ds.projectAlias().isEmpty()) {
            List<Integer> projectIds = getUserProjectIds(loginUser.getUserId());
            if (projectIds.isEmpty()) {
                condition.append(" AND 1 = 0");
            } else {
                String ids = projectIds.stream().map(String::valueOf)
                        .collect(Collectors.joining(","));
                condition.append(String.format(" AND %s.project_id IN (%s)",
                        ds.projectAlias(), ids));
            }
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
