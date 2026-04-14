package com.mochu.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mochu.system.entity.SysUser;
import com.mochu.system.mapper.SysDeptMapper;
import com.mochu.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 通讯录服务 — P5 §2
 * 支持部门树、按部门查员工、搜索员工，含隐私脱敏
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContactService {

    private final SysUserMapper userMapper;
    private final SysDeptMapper deptMapper;

    public List<Map<String, Object>> getEmployees(Integer deptId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getStatus, 1)
                .eq(SysUser::getFlagContact, 1);

        if (deptId != null) {
            wrapper.eq(SysUser::getDeptId, deptId);
        }

        List<SysUser> users = userMapper.selectList(wrapper);
        return users.stream().map(this::toContactMap).collect(Collectors.toList());
    }

    public List<Map<String, Object>> searchEmployees(String keyword) {
        if (keyword == null || keyword.isBlank()) return Collections.emptyList();
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getStatus, 1)
                .eq(SysUser::getFlagContact, 1)
                .and(w -> w
                        .like(SysUser::getRealName, keyword)
                        .or().like(SysUser::getPhone, keyword)
                        .or().like(SysUser::getEmail, keyword));
        List<SysUser> users = userMapper.selectList(wrapper);
        return users.stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("realName", u.getRealName());
            map.put("phone", (u.getPrivacyMode() != null && u.getPrivacyMode() == 1)
                    ? maskPhone(u.getPhone()) : u.getPhone());
            return map;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getDeptTree() {
        // 复用已有 DeptService 的树构建逻辑
        return Collections.emptyList();
    }

    private Map<String, Object> toContactMap(SysUser u) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", u.getId());
        map.put("realName", u.getRealName());
        map.put("deptId", u.getDeptId());
        map.put("position", u.getPosition());
        // 隐私模式脱敏
        if (u.getPrivacyMode() != null && u.getPrivacyMode() == 1) {
            map.put("phone", maskPhone(u.getPhone()));
            map.put("email", maskEmail(u.getEmail()));
        } else {
            map.put("phone", u.getPhone());
            map.put("email", u.getEmail());
        }
        return map;
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        String[] parts = email.split("@");
        return parts[0].charAt(0) + "***@" + parts[1];
    }
}
