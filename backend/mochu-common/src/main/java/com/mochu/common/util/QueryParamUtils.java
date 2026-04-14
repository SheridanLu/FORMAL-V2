package com.mochu.common.util;

import com.mochu.common.exception.BusinessException;

import java.util.Set;

/**
 * 查询参数规范化工具 — P5 §6.1
 * 分页 size 强制 <= 100, 排序字段白名单校验
 */
public class QueryParamUtils {

    private static final int MAX_SIZE = 100;
    private static final int DEFAULT_SIZE = 20;

    /** 通用排序白名单 */
    private static final Set<String> SORT_WHITELIST = Set.of(
            "created_at", "updated_at", "id", "project_no", "contract_no",
            "amount", "amount_with_tax", "status", "plan_start_date",
            "plan_end_date", "sign_date", "inbound_date"
    );

    /**
     * 规范化分页 size — 超过100强制取100
     */
    public static int normalizeSize(Integer size) {
        if (size == null || size < 1) return DEFAULT_SIZE;
        return Math.min(size, MAX_SIZE);
    }

    /**
     * 校验排序字段是否在白名单中
     */
    public static void validateSortField(String sortField) {
        if (sortField != null && !sortField.isBlank()
                && !SORT_WHITELIST.contains(sortField)) {
            throw new BusinessException(400,
                    "不支持的排序字段: " + sortField);
        }
    }
}
