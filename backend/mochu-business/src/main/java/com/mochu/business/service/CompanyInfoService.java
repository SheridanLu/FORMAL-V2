package com.mochu.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochu.business.dto.CompanyInfoDTO;
import com.mochu.business.entity.BizCompanyInfo;
import com.mochu.business.mapper.BizCompanyInfoMapper;
import com.mochu.common.exception.BusinessException;
import com.mochu.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 公司/单位信息 Service
 *
 * <p>V3.0 §3.5: 合同/供应商表单的甲乙方自动填充数据源
 */
@Service
@RequiredArgsConstructor
public class CompanyInfoService {

    private final BizCompanyInfoMapper companyInfoMapper;

    /**
     * 分页查询
     */
    public PageResult<BizCompanyInfo> list(String companyName, int page, int size) {
        Page<BizCompanyInfo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<BizCompanyInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(companyName)) {
            wrapper.like(BizCompanyInfo::getCompanyName, companyName);
        }
        wrapper.orderByDesc(BizCompanyInfo::getCreatedAt);
        companyInfoMapper.selectPage(pageParam, wrapper);
        return new PageResult<>(pageParam.getRecords(), pageParam.getTotal(), page, size);
    }

    /**
     * 按名称模糊搜索（前端自动填充下拉用）
     */
    public List<BizCompanyInfo> search(String keyword) {
        LambdaQueryWrapper<BizCompanyInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(BizCompanyInfo::getCompanyName, keyword);
        }
        wrapper.orderByDesc(BizCompanyInfo::getCreatedAt);
        wrapper.last("LIMIT 20");
        return companyInfoMapper.selectList(wrapper);
    }

    /**
     * 按名称精确查找（自动填充时回填详情）
     */
    public BizCompanyInfo getByName(String companyName) {
        if (!StringUtils.hasText(companyName)) return null;
        LambdaQueryWrapper<BizCompanyInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizCompanyInfo::getCompanyName, companyName);
        wrapper.last("LIMIT 1");
        return companyInfoMapper.selectOne(wrapper);
    }

    public BizCompanyInfo getById(Integer id) {
        return companyInfoMapper.selectById(id);
    }

    public void create(CompanyInfoDTO dto) {
        // 检查名称唯一
        BizCompanyInfo existing = getByName(dto.getCompanyName());
        if (existing != null) {
            throw new BusinessException("公司名称已存在: " + dto.getCompanyName());
        }
        BizCompanyInfo entity = new BizCompanyInfo();
        BeanUtils.copyProperties(dto, entity);
        companyInfoMapper.insert(entity);
    }

    public void update(Integer id, CompanyInfoDTO dto) {
        BizCompanyInfo entity = companyInfoMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("公司信息不存在");
        }
        // 检查名称唯一（排除自身）
        BizCompanyInfo existing = getByName(dto.getCompanyName());
        if (existing != null && !existing.getId().equals(id)) {
            throw new BusinessException("公司名称已存在: " + dto.getCompanyName());
        }
        BeanUtils.copyProperties(dto, entity, "id");
        companyInfoMapper.updateById(entity);
    }

    public void delete(Integer id) {
        BizCompanyInfo entity = companyInfoMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("公司信息不存在");
        }
        companyInfoMapper.deleteById(id);
    }
}
