package com.mochu.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mochu.system.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * 角色表 Mapper
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据用户ID查询角色编码列表
     */
    @Select("SELECT r.role_code FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON ur.role_id = r.id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0")
    List<String> selectRoleCodesByUserId(@Param("userId") Integer userId);

    /**
     * 根据用户ID查询角色列表
     */
    @Select("SELECT r.* FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON ur.role_id = r.id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0")
    List<SysRole> selectRolesByUserId(@Param("userId") Integer userId);

    /**
     * 根据用户ID查询权限编码列表
     */
    @Select("SELECT DISTINCT p.perm_code FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON rp.permission_id = p.id " +
            "INNER JOIN sys_role r ON r.id = rp.role_id " +
            "INNER JOIN sys_user_role ur ON ur.role_id = rp.role_id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0 AND r.status = 1")
    Set<String> selectPermCodesByUserId(@Param("userId") Integer userId);

    /**
     * 查询用户最高数据权限级别（值越小权限越大，1=全部）
     */
    @Select("SELECT MIN(r.data_scope) FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON ur.role_id = r.id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0")
    Integer selectMaxDataScopeByUserId(@Param("userId") Integer userId);
}
