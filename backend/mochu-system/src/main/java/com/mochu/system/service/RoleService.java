package com.mochu.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochu.common.constant.Constants;
import com.mochu.common.exception.BusinessException;
import com.mochu.common.result.PageResult;
import com.mochu.system.dto.RoleDTO;
import com.mochu.system.entity.SysRole;
import com.mochu.system.entity.SysRolePermission;
import com.mochu.system.entity.SysUserRole;
import com.mochu.system.mapper.SysRoleMapper;
import com.mochu.system.mapper.SysRolePermissionMapper;
import com.mochu.system.mapper.SysUserRoleMapper;
import com.mochu.system.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色服务 — 对照 V3.2 角色管理
 */
@Service
@RequiredArgsConstructor
public class RoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final SysUserRoleMapper sysUserRoleMapper;

    // Hardcoded V3.2 mutual exclusion pairs (can be made configurable later)
    private static final List<List<String>> MUTUAL_EXCLUSION_PAIRS = List.of(
            List.of("PURCHASE", "FINANCE"),
            List.of("PURCHASE", "BUDGET"),
            List.of("HR", "FINANCE"),
            List.of("PROJ_MGR", "FINANCE")
    );

    public List<List<String>> getMutualExclusions() {
        return MUTUAL_EXCLUSION_PAIRS;
    }

    /**
     * 角色分页查询
     */
    public PageResult<RoleVO> listRoles(String roleName, Integer status, Integer page, Integer size) {
        if (page == null || page < 1) page = Constants.DEFAULT_PAGE;
        if (size == null || size < 1) size = Constants.DEFAULT_SIZE;

        Page<SysRole> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (roleName != null && !roleName.isBlank()) {
            wrapper.like(SysRole::getRoleName, roleName);
        }
        if (status != null) {
            wrapper.eq(SysRole::getStatus, status);
        }
        wrapper.orderByAsc(SysRole::getId);

        sysRoleMapper.selectPage(pageParam, wrapper);

        List<RoleVO> voList = pageParam.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return new PageResult<>(voList, pageParam.getTotal(), page, size);
    }

    /**
     * 角色详情
     */
    public RoleVO getRoleById(Integer id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        return toVO(role);
    }

    /**
     * 创建角色
     */
    @Transactional
    public Integer createRole(RoleDTO dto) {
        // 检查编码唯一
        Long count = sysRoleMapper.selectCount(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, dto.getRoleCode())
        );
        if (count > 0) {
            throw new BusinessException("角色编码已存在");
        }

        SysRole role = new SysRole();
        BeanUtils.copyProperties(dto, role);
        if (role.getDataScope() == null) {
            role.setDataScope(4); // 默认仅个人
        }
        if (role.getStatus() == null) {
            role.setStatus(1);
        }
        sysRoleMapper.insert(role);

        // 保存权限关联
        saveRolePermissions(role.getId(), dto.getPermissionIds());

        return role.getId();
    }

    /**
     * 更新角色
     */
    @Transactional
    public void updateRole(RoleDTO dto) {
        SysRole role = sysRoleMapper.selectById(dto.getId());
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        role.setRoleName(dto.getRoleName());
        role.setDataScope(dto.getDataScope());
        role.setRemark(dto.getRemark());
        if (dto.getStatus() != null) {
            role.setStatus(dto.getStatus());
        }
        sysRoleMapper.updateById(role);

        // 重建权限关联
        if (dto.getPermissionIds() != null) {
            sysRolePermissionMapper.delete(
                    new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, role.getId())
            );
            saveRolePermissions(role.getId(), dto.getPermissionIds());
        }
    }

    /**
     * 删除角色
     */
    public void deleteRole(Integer id) {
        sysRoleMapper.deleteById(id);
    }

    /**
     * 查询角色权限ID列表 — V3.2 §5.9.5
     */
    public List<Integer> getRolePermissionIds(Integer roleId) {
        List<SysRolePermission> rps = sysRolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, roleId)
        );
        return rps.stream().map(SysRolePermission::getPermissionId).collect(Collectors.toList());
    }

    /**
     * 配置角色权限 — V3.2 §5.9.5
     */
    @Transactional
    public void updateRolePermissions(Integer roleId, List<Integer> permissionIds) {
        sysRolePermissionMapper.delete(
                new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, roleId)
        );
        saveRolePermissions(roleId, permissionIds);
    }

    /**
     * 更新角色数据权限 — V3.2 §5.9.2
     */
    public void updateDataScope(Integer roleId, Integer dataScope) {
        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null) throw new BusinessException("角色不存在");
        role.setDataScope(dataScope);
        sysRoleMapper.updateById(role);
    }

    /**
     * P5: 分配角色给用户 — 含互斥校验
     */
    @Transactional
    public void assignRolesToUser(Integer userId, List<Integer> roleIds) {
        // 查询角色编码
        List<SysRole> roles = sysRoleMapper.selectBatchIds(roleIds);
        Set<String> roleCodes = roles.stream()
                .map(SysRole::getRoleCode).collect(Collectors.toSet());

        // 互斥校验
        RoleMutexValidator.validateAll(roleCodes);

        // 先删除旧关联
        sysUserRoleMapper.delete(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));

        // 写入新关联
        for (Integer roleId : roleIds) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            sysUserRoleMapper.insertUserRole(ur);
        }
    }

    private void saveRolePermissions(Integer roleId, List<Integer> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return;
        }
        for (Integer permId : permissionIds) {
            SysRolePermission rp = new SysRolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(permId);
            sysRolePermissionMapper.insertRolePermission(rp);
        }
    }

    private RoleVO toVO(SysRole role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);

        // 查询权限ID列表
        List<SysRolePermission> rps = sysRolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, role.getId())
        );
        vo.setPermissionIds(rps.stream().map(SysRolePermission::getPermissionId).collect(Collectors.toList()));

        return vo;
    }
}
