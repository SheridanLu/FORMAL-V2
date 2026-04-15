package com.mochu.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochu.common.exception.BusinessException;
import com.mochu.common.result.PageResult;
import com.mochu.system.dto.BannerDTO;
import com.mochu.system.entity.SysBanner;
import com.mochu.system.mapper.SysBannerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 首页轮播图 Service
 */
@Service
@RequiredArgsConstructor
public class BannerService {

    private final SysBannerMapper bannerMapper;

    /**
     * 管理端分页列表
     */
    public PageResult<SysBanner> list(String title, Integer status, int page, int size) {
        Page<SysBanner> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SysBanner> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(title)) {
            wrapper.like(SysBanner::getTitle, title);
        }
        if (status != null) {
            wrapper.eq(SysBanner::getStatus, status);
        }
        wrapper.orderByDesc(SysBanner::getSortOrder).orderByDesc(SysBanner::getCreatedAt);
        bannerMapper.selectPage(pageParam, wrapper);
        return new PageResult<>(pageParam.getRecords(), pageParam.getTotal(), page, size);
    }

    /**
     * 前台展示列表（只返回启用且在有效期内的轮播图）
     */
    public List<SysBanner> listActive() {
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<SysBanner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysBanner::getStatus, 1)
               .and(w -> w.isNull(SysBanner::getStartTime).or().le(SysBanner::getStartTime, now))
               .and(w -> w.isNull(SysBanner::getEndTime).or().ge(SysBanner::getEndTime, now))
               .orderByDesc(SysBanner::getSortOrder);
        return bannerMapper.selectList(wrapper);
    }

    public SysBanner getById(Integer id) {
        return bannerMapper.selectById(id);
    }

    public void create(BannerDTO dto) {
        SysBanner entity = new SysBanner();
        BeanUtils.copyProperties(dto, entity);
        if (entity.getSortOrder() == null) entity.setSortOrder(0);
        if (entity.getStatus() == null) entity.setStatus(1);
        bannerMapper.insert(entity);
    }

    public void update(Integer id, BannerDTO dto) {
        SysBanner entity = bannerMapper.selectById(id);
        if (entity == null) throw new BusinessException("轮播图不存在");
        BeanUtils.copyProperties(dto, entity, "id");
        bannerMapper.updateById(entity);
    }

    public void delete(Integer id) {
        SysBanner entity = bannerMapper.selectById(id);
        if (entity == null) throw new BusinessException("轮播图不存在");
        bannerMapper.deleteById(id);
    }

    /**
     * 切换启用/禁用
     */
    public void toggleStatus(Integer id) {
        SysBanner entity = bannerMapper.selectById(id);
        if (entity == null) throw new BusinessException("轮播图不存在");
        entity.setStatus(entity.getStatus() == 1 ? 0 : 1);
        bannerMapper.updateById(entity);
    }
}
