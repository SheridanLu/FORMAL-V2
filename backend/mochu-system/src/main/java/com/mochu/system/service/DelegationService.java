package com.mochu.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mochu.common.constant.Constants;
import com.mochu.common.exception.BusinessException;
import com.mochu.common.result.PageResult;
import com.mochu.common.security.SecurityUtils;
import com.mochu.system.dto.DelegationDTO;
import com.mochu.system.entity.SysDelegation;
import com.mochu.system.mapper.SysDelegationMapper;
import com.mochu.system.mapper.SysUserMapper;
import com.mochu.system.vo.DelegationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 委托代理服务
 */
@Service
@RequiredArgsConstructor
public class DelegationService {

    private final SysDelegationMapper delegationMapper;
    private final SysUserMapper userMapper;
    private final ObjectMapper objectMapper;

    public PageResult<DelegationVO> list(Integer delegatorId, Integer status, Integer page, Integer size) {
        if (page == null || page < 1) page = Constants.DEFAULT_PAGE;
        if (size == null || size < 1) size = Constants.DEFAULT_SIZE;

        Page<SysDelegation> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SysDelegation> wrapper = new LambdaQueryWrapper<>();

        if (delegatorId != null) {
            wrapper.eq(SysDelegation::getDelegatorId, delegatorId);
        }
        if (status != null) {
            wrapper.eq(SysDelegation::getStatus, status);
        }
        wrapper.orderByDesc(SysDelegation::getCreatedAt);
        delegationMapper.selectPage(pageParam, wrapper);

        List<DelegationVO> voList = pageParam.getRecords().stream().map(d -> {
            DelegationVO vo = new DelegationVO();
            BeanUtils.copyProperties(d, vo);
            // Resolve user names
            var delegator = userMapper.selectById(d.getDelegatorId());
            var delegatee = userMapper.selectById(d.getDelegateeId());
            if (delegator != null) vo.setDelegatorName(delegator.getRealName());
            if (delegatee != null) vo.setDelegateeName(delegatee.getRealName());
            return vo;
        }).toList();

        return new PageResult<>(voList, pageParam.getTotal(), page, size);
    }

    public void create(DelegationDTO dto) {
        Integer currentUserId = SecurityUtils.getCurrentUserId();
        SysDelegation entity = new SysDelegation();
        entity.setDelegatorId(currentUserId);
        entity.setDelegateeId(dto.getDelegateeId());
        try {
            if (dto.getPermissionCodes() == null || dto.getPermissionCodes().isEmpty()) {
                entity.setPermissionCodes("[]");
            } else {
                entity.setPermissionCodes(objectMapper.writeValueAsString(dto.getPermissionCodes()));
            }
        } catch (JsonProcessingException e) {
            entity.setPermissionCodes("[]");
        }
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setRemark(dto.getRemark());
        entity.setStatus(1);
        delegationMapper.insert(entity);
    }

    public void revoke(Integer id) {
        SysDelegation entity = delegationMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("委托记录不存在");
        }
        entity.setStatus(0);
        delegationMapper.updateById(entity);
    }

    public void delete(Integer id) {
        delegationMapper.deleteById(id);
    }
}
