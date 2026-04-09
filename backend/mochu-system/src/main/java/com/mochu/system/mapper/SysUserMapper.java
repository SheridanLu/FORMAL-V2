package com.mochu.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mochu.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表 Mapper — 对照 V3.2 附录P.1
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
