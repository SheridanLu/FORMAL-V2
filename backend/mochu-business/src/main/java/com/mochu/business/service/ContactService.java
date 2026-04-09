package com.mochu.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochu.business.dto.ExternalContactDTO;
import com.mochu.business.entity.BizExternalContact;
import com.mochu.business.mapper.BizExternalContactMapper;
import com.mochu.common.constant.Constants;
import com.mochu.common.exception.BusinessException;
import com.mochu.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final BizExternalContactMapper externalContactMapper;

    public PageResult<BizExternalContact> listExternal(String contactType, String keyword, Integer page, Integer size) {
        if (page == null || page < 1) page = Constants.DEFAULT_PAGE;
        if (size == null || size < 1) size = Constants.DEFAULT_SIZE;

        Page<BizExternalContact> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<BizExternalContact> wrapper = new LambdaQueryWrapper<>();

        if (contactType != null && !contactType.isBlank()) {
            wrapper.eq(BizExternalContact::getContactType, contactType);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(BizExternalContact::getName, keyword)
                    .or().like(BizExternalContact::getCompany, keyword));
        }
        wrapper.orderByDesc(BizExternalContact::getCreatedAt);

        externalContactMapper.selectPage(pageParam, wrapper);
        return new PageResult<>(pageParam.getRecords(), pageParam.getTotal(), page, size);
    }

    public BizExternalContact getExternalById(Integer id) {
        return externalContactMapper.selectById(id);
    }

    public void createExternal(ExternalContactDTO dto) {
        BizExternalContact entity = new BizExternalContact();
        BeanUtils.copyProperties(dto, entity);
        externalContactMapper.insert(entity);
    }

    public void updateExternal(Integer id, ExternalContactDTO dto) {
        BizExternalContact entity = externalContactMapper.selectById(id);
        if (entity == null) throw new BusinessException("联系人不存在");
        BeanUtils.copyProperties(dto, entity, "id");
        externalContactMapper.updateById(entity);
    }

    public void deleteExternal(Integer id) {
        externalContactMapper.deleteById(id);
    }
}
