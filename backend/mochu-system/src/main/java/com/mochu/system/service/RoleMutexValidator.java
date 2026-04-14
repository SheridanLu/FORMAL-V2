package com.mochu.system.service;

import com.mochu.common.exception.BusinessException;

import java.util.*;

/**
 * 角色互斥矩阵校验 — P5 §7.3
 * PURCHASE <-> FINANCE, PURCHASE <-> BUDGET, PROJ_MGR <-> FINANCE, HR <-> FINANCE
 * GM 不参与互斥, DATA/BASE/SOFT/TEAM_MEMBER/LEGAL 不参与互斥
 */
public class RoleMutexValidator {

    /** 互斥对 */
    private static final List<Set<String>> MUTEX_PAIRS = List.of(
            Set.of("PURCHASE", "FINANCE"),
            Set.of("PURCHASE", "BUDGET"),
            Set.of("PROJ_MGR", "FINANCE"),
            Set.of("HR", "FINANCE")
    );

    /**
     * 校验角色分配是否违反互斥规则
     * @param currentRoles 用户当前角色编码集合
     * @param newRoleCode  要新分配的角色编码
     * @throws BusinessException 互斥冲突时抛出
     */
    public static void validate(Set<String> currentRoles, String newRoleCode) {
        for (Set<String> pair : MUTEX_PAIRS) {
            if (pair.contains(newRoleCode)) {
                for (String existing : currentRoles) {
                    if (pair.contains(existing) && !existing.equals(newRoleCode)) {
                        throw new BusinessException(400,
                                String.format("角色[%s]与[%s]互斥，不可同时分配",
                                        newRoleCode, existing));
                    }
                }
            }
        }
    }

    /**
     * 批量校验一组角色是否有内部互斥
     */
    public static void validateAll(Set<String> roles) {
        List<String> roleList = new ArrayList<>(roles);
        for (int i = 0; i < roleList.size(); i++) {
            for (int j = i + 1; j < roleList.size(); j++) {
                Set<String> check = Set.of(roleList.get(i), roleList.get(j));
                for (Set<String> pair : MUTEX_PAIRS) {
                    if (pair.equals(check)) {
                        throw new BusinessException(400,
                                String.format("角色[%s]与[%s]互斥",
                                        roleList.get(i), roleList.get(j)));
                    }
                }
            }
        }
    }
}
