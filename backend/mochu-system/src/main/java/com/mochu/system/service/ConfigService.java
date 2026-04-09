package com.mochu.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochu.common.constant.Constants;
import com.mochu.common.exception.BusinessException;
import com.mochu.common.result.PageResult;
import com.mochu.system.dto.ConfigDTO;
import com.mochu.system.dto.ConfigQueryDTO;
import com.mochu.system.entity.SysConfig;
import com.mochu.system.mapper.SysConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 系统配置服务
 */
@Service
@RequiredArgsConstructor
public class ConfigService {

    private final SysConfigMapper configMapper;

    public PageResult<SysConfig> list(ConfigQueryDTO query) {
        int page = (query.getPage() == null || query.getPage() < 1) ? Constants.DEFAULT_PAGE : query.getPage();
        int size = (query.getSize() == null || query.getSize() < 1) ? Constants.DEFAULT_SIZE : query.getSize();

        Page<SysConfig> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();

        if (query.getConfigKey() != null && !query.getConfigKey().isBlank()) {
            wrapper.like(SysConfig::getConfigKey, query.getConfigKey());
        }
        if (query.getConfigGroup() != null && !query.getConfigGroup().isBlank()) {
            wrapper.eq(SysConfig::getConfigGroup, query.getConfigGroup());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SysConfig::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(SysConfig::getCreatedAt);

        configMapper.selectPage(pageParam, wrapper);
        return new PageResult<>(pageParam.getRecords(), pageParam.getTotal(), page, size);
    }

    public SysConfig getById(Integer id) {
        return configMapper.selectById(id);
    }

    public SysConfig getByKey(String key) {
        return configMapper.selectOne(
                new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, key));
    }

    public String getValueByKey(String key, String defaultValue) {
        SysConfig config = getByKey(key);
        return config != null ? config.getConfigValue() : defaultValue;
    }

    public void create(ConfigDTO dto) {
        SysConfig config = new SysConfig();
        config.setConfigKey(dto.getConfigKey());
        config.setConfigValue(dto.getConfigValue());
        config.setConfigDesc(dto.getConfigDesc());
        config.setConfigGroup(dto.getConfigGroup());
        config.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        configMapper.insert(config);
    }

    public void update(Integer id, ConfigDTO dto) {
        SysConfig config = configMapper.selectById(id);
        if (config == null) {
            throw new BusinessException("配置不存在");
        }
        config.setConfigKey(dto.getConfigKey());
        config.setConfigValue(dto.getConfigValue());
        config.setConfigDesc(dto.getConfigDesc());
        config.setConfigGroup(dto.getConfigGroup());
        if (dto.getStatus() != null) {
            config.setStatus(dto.getStatus());
        }
        configMapper.updateById(config);
    }

    public void delete(Integer id) {
        configMapper.deleteById(id);
    }
}
