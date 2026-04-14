package com.mochu.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mochu.business.entity.*;
import com.mochu.business.mapper.*;
import com.mochu.system.entity.SysConfig;
import com.mochu.system.entity.SysTodo;
import com.mochu.system.mapper.SysConfigMapper;
import com.mochu.system.mapper.SysTodoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialPriceService {

    private final BizMaterialBasePriceMapper basePriceMapper;
    private final BizMaterialPriceHistoryMapper historyMapper;
    private final SysConfigMapper configMapper;
    private final SysTodoMapper todoMapper;

    /** 默认价格波动预警阈值 */
    private static final BigDecimal DEFAULT_THRESHOLD = new BigDecimal("10");

    /**
     * 支出合同审批通过后更新基准价
     * @param materialId 材料ID
     * @param newPrice   合同中的新单价
     * @param contractId 合同ID
     * @param operatorId 操作人
     */
    @Transactional
    public void updateBasePriceFromContract(Integer materialId, BigDecimal newPrice,
                                             Integer contractId, Integer operatorId) {
        // 查询当前基准价
        BizMaterialBasePrice current = getCurrentBasePrice(materialId);
        BigDecimal oldPrice = current != null ? current.getBasePrice() : BigDecimal.ZERO;

        // 计算波动幅度
        BigDecimal changeRate = BigDecimal.ZERO;
        if (oldPrice.compareTo(BigDecimal.ZERO) > 0) {
            changeRate = newPrice.subtract(oldPrice).abs()
                    .divide(oldPrice, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // 记录价格变动历史
        BizMaterialPriceHistory history = new BizMaterialPriceHistory();
        history.setMaterialId(materialId);
        history.setOldPrice(oldPrice);
        history.setNewPrice(newPrice);
        history.setChangeRate(changeRate);
        history.setChangeReason("支出合同审批通过自动更新");
        history.setSource("contract");
        history.setSourceId(contractId);
        history.setCreatorId(operatorId);
        history.setCreatedAt(LocalDateTime.now());
        historyMapper.insert(history);

        // 更新/新建基准价
        BizMaterialBasePrice basePrice = new BizMaterialBasePrice();
        basePrice.setMaterialId(materialId);
        basePrice.setBasePrice(newPrice);
        basePrice.setEffectiveDate(LocalDate.now());
        basePrice.setSource("contract");
        basePrice.setSourceId(contractId);
        basePrice.setCreatorId(operatorId);

        if (current != null) {
            basePrice.setId(current.getId());
            basePriceMapper.updateById(basePrice);
        } else {
            basePriceMapper.insert(basePrice);
        }

        // 检查是否触发预警
        BigDecimal threshold = getWarningThreshold();
        if (changeRate.compareTo(threshold) > 0) {
            createPriceWarningTodo(materialId, oldPrice, newPrice, changeRate);
        }

        log.info("材料基准价已更新: materialId={}, {} -> {}, 波动{}%",
                materialId, oldPrice, newPrice, changeRate);
    }

    /**
     * 获取预警阈值（从sys_config读取，默认10%）
     */
    private BigDecimal getWarningThreshold() {
        try {
            SysConfig config = configMapper.selectOne(
                    new LambdaQueryWrapper<SysConfig>()
                            .eq(SysConfig::getConfigKey, "material.price.warning.threshold"));
            if (config != null && config.getConfigValue() != null) {
                return new BigDecimal(config.getConfigValue());
            }
        } catch (Exception e) {
            log.warn("读取价格预警阈值失败，使用默认值", e);
        }
        return DEFAULT_THRESHOLD;
    }

    /**
     * 创建价格预警待办
     */
    private void createPriceWarningTodo(Integer materialId,
            BigDecimal oldPrice, BigDecimal newPrice, BigDecimal changeRate) {
        SysTodo todo = new SysTodo();
        // 通知采购角色用户（简化处理：通知管理员）
        todo.setUserId(1);
        todo.setTitle(String.format("【材料价格预警】材料ID:%d 价格波动%.2f%%",
                materialId, changeRate));
        todo.setContent(String.format("材料基准价变动: %.2f → %.2f，波动幅度 %.2f%% 超过阈值",
                oldPrice, newPrice, changeRate));
        todo.setBizType("material_price_warning");
        todo.setBizId(materialId);
        todo.setStatus(0);
        todoMapper.insert(todo);
    }

    /**
     * 获取材料当前基准价
     */
    public BizMaterialBasePrice getCurrentBasePrice(Integer materialId) {
        return basePriceMapper.selectOne(
                new LambdaQueryWrapper<BizMaterialBasePrice>()
                        .eq(BizMaterialBasePrice::getMaterialId, materialId)
                        .eq(BizMaterialBasePrice::getDeleted, 0)
                        .orderByDesc(BizMaterialBasePrice::getEffectiveDate)
                        .last("LIMIT 1"));
    }
}
