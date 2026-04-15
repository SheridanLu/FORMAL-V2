package com.mochu.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochu.business.dto.EquipmentDTO;
import com.mochu.business.entity.BizEquipment;
import com.mochu.business.mapper.BizEquipmentMapper;
import com.mochu.common.exception.BusinessException;
import com.mochu.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 设备管理 Service
 *
 * <p>V3.0: 设备台账、分配到项目、状态变更、保养提醒
 */
@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final BizEquipmentMapper equipmentMapper;
    private final NoGeneratorService noGeneratorService;

    /**
     * 分页查询
     */
    public PageResult<BizEquipment> list(String equipmentName, String category, String status,
                                         Integer projectId, int page, int size) {
        Page<BizEquipment> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<BizEquipment> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(equipmentName)) {
            wrapper.like(BizEquipment::getEquipmentName, equipmentName);
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(BizEquipment::getCategory, category);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(BizEquipment::getStatus, status);
        }
        if (projectId != null) {
            wrapper.eq(BizEquipment::getProjectId, projectId);
        }
        wrapper.orderByDesc(BizEquipment::getCreatedAt);
        equipmentMapper.selectPage(pageParam, wrapper);
        return new PageResult<>(pageParam.getRecords(), pageParam.getTotal(), page, size);
    }

    public BizEquipment getById(Integer id) {
        return equipmentMapper.selectById(id);
    }

    public void create(EquipmentDTO dto) {
        BizEquipment entity = new BizEquipment();
        BeanUtils.copyProperties(dto, entity);
        entity.setEquipmentNo(noGeneratorService.generate("EQ"));
        if (entity.getStatus() == null) entity.setStatus("idle");
        equipmentMapper.insert(entity);
    }

    public void update(Integer id, EquipmentDTO dto) {
        BizEquipment entity = equipmentMapper.selectById(id);
        if (entity == null) throw new BusinessException("设备不存在");
        BeanUtils.copyProperties(dto, entity, "id", "equipmentNo");
        equipmentMapper.updateById(entity);
    }

    /**
     * 分配到项目
     */
    public void assignToProject(Integer id, Integer projectId, String location) {
        BizEquipment entity = equipmentMapper.selectById(id);
        if (entity == null) throw new BusinessException("设备不存在");
        entity.setProjectId(projectId);
        entity.setLocation(location);
        entity.setStatus("in_use");
        equipmentMapper.updateById(entity);
    }

    /**
     * 变更状态
     */
    public void updateStatus(Integer id, String status) {
        BizEquipment entity = equipmentMapper.selectById(id);
        if (entity == null) throw new BusinessException("设备不存在");
        entity.setStatus(status);
        if ("idle".equals(status)) {
            entity.setProjectId(null);
        }
        equipmentMapper.updateById(entity);
    }

    public void delete(Integer id) {
        BizEquipment entity = equipmentMapper.selectById(id);
        if (entity == null) throw new BusinessException("设备不存在");
        if ("in_use".equals(entity.getStatus())) {
            throw new BusinessException("使用中的设备不允许删除");
        }
        equipmentMapper.deleteById(id);
    }
}
