package com.mochu.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mochu.system.entity.SysRolePermission;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 角色权限关联 Mapper
 */
@Mapper
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

    @Insert("INSERT INTO sys_role_permission (role_id, permission_id) VALUES (#{rp.roleId}, #{rp.permissionId})")
    int insertRolePermission(@Param("rp") SysRolePermission rp);
}
