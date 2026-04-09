package com.mochu.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mochu.system.entity.SysUserRole;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户角色关联 Mapper
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    @Insert("INSERT INTO sys_user_role (user_id, role_id) VALUES (#{ur.userId}, #{ur.roleId})")
    int insertUserRole(@Param("ur") SysUserRole ur);
}
