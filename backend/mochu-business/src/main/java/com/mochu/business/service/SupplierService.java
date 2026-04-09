package com.mochu.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochu.business.dto.SupplierDTO;
import com.mochu.business.entity.BizSupplier;
import com.mochu.business.mapper.BizSupplierMapper;
import com.mochu.common.constant.Constants;
import com.mochu.common.exception.BusinessException;
import com.mochu.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final BizSupplierMapper supplierMapper;

    public PageResult<BizSupplier> list(String supplierName, String status, Integer page, Integer size) {
        if (page == null || page < 1) page = Constants.DEFAULT_PAGE;
        if (size == null || size < 1) size = Constants.DEFAULT_SIZE;

        Page<BizSupplier> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<BizSupplier> wrapper = new LambdaQueryWrapper<>();

        if (supplierName != null && !supplierName.isBlank()) {
            wrapper.like(BizSupplier::getSupplierName, supplierName);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(BizSupplier::getStatus, status);
        }
        wrapper.orderByDesc(BizSupplier::getCreatedAt);

        supplierMapper.selectPage(pageParam, wrapper);
        return new PageResult<>(pageParam.getRecords(), pageParam.getTotal(), page, size);
    }

    public BizSupplier getById(Integer id) {
        return supplierMapper.selectById(id);
    }

    public void create(SupplierDTO dto) {
        BizSupplier entity = new BizSupplier();
        BeanUtils.copyProperties(dto, entity);
        if (entity.getStatus() == null) entity.setStatus("active");
        supplierMapper.insert(entity);
    }

    public void update(Integer id, SupplierDTO dto) {
        BizSupplier entity = supplierMapper.selectById(id);
        if (entity == null) throw new BusinessException("供应商不存在");
        BeanUtils.copyProperties(dto, entity, "id");
        supplierMapper.updateById(entity);
    }

    public void delete(Integer id) {
        supplierMapper.deleteById(id);
    }

    public List<BizSupplier> listAll() {
        return supplierMapper.selectList(
                new LambdaQueryWrapper<BizSupplier>()
                        .select(BizSupplier::getId, BizSupplier::getSupplierName)
                        .eq(BizSupplier::getStatus, "active")
                        .orderByAsc(BizSupplier::getSupplierName));
    }
}
