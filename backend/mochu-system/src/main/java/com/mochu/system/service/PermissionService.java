package com.mochu.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mochu.system.entity.SysPermission;
import com.mochu.system.mapper.SysPermissionMapper;
import com.mochu.system.vo.PermissionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权限服务
 */
@Service
@RequiredArgsConstructor
public class PermissionService {

    private final SysPermissionMapper sysPermissionMapper;

    /**
     * 查询全部权限列表
     */
    public List<PermissionVO> listAll() {
        List<SysPermission> permissions = sysPermissionMapper.selectList(
                new LambdaQueryWrapper<SysPermission>().orderByAsc(SysPermission::getModule, SysPermission::getId)
        );
        return permissions.stream().map(this::toVO).collect(Collectors.toList());
    }

    /**
     * 按模块分组查询
     */
    public List<PermissionVO> listByModule(String module) {
        List<SysPermission> permissions = sysPermissionMapper.selectList(
                new LambdaQueryWrapper<SysPermission>()
                        .eq(SysPermission::getModule, module)
                        .orderByAsc(SysPermission::getId)
        );
        return permissions.stream().map(this::toVO).collect(Collectors.toList());
    }

    /**
     * 按模块分组 — 树形结构
     */
    public List<Map<String, Object>> listTree() {
        List<PermissionVO> all = listAll();
        // Group by module
        Map<String, List<PermissionVO>> grouped = all.stream()
                .collect(Collectors.groupingBy(PermissionVO::getModule));
        List<Map<String, Object>> tree = new ArrayList<>();
        for (Map.Entry<String, List<PermissionVO>> entry : grouped.entrySet()) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", "mod_" + entry.getKey());
            node.put("perm_name", entry.getKey());
            node.put("children", entry.getValue());
            tree.add(node);
        }
        return tree;
    }

    private PermissionVO toVO(SysPermission perm) {
        PermissionVO vo = new PermissionVO();
        BeanUtils.copyProperties(perm, vo);
        return vo;
    }
}
