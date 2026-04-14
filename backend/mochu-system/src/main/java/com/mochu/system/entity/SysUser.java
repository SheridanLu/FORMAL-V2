package com.mochu.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户表 — 对照 V3.2 附录P.1
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    /** 登录名 */
    private String username;

    /** 姓名 */
    private String realName;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 部门ID */
    private Integer deptId;

    /** 职位 */
    private String position;

    /** BCrypt加盐哈希密码 */
    private String passwordHash;

    /** 头像URL */
    private String avatar;

    /** 1启用/0禁用 */
    private Integer status;

    /** 是否在通讯录显示 */
    private Integer flagContact;

    /** 隐私模式 */
    private Integer privacyMode;

    /** 登录失败次数 */
    private Integer loginAttempts;

    /** 最后登录时间 */
    private LocalDateTime lastLoginTime;

    /** 锁定到期时间 */
    private LocalDateTime lockUntil;

    /** 密码最后修改时间 */
    private LocalDateTime passwordChangedAt;

    /** 首次登录是否强制改密 */
    private Integer forceChangePwd;

    /** 企业微信用户ID */
    private String wxUserid;
}
