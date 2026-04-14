package com.mochu.business.service;

import com.mochu.business.entity.BizInventoryRecord;
import com.mochu.business.mapper.BizInventoryRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryRecordService {

    private final BizInventoryRecordMapper recordMapper;

    /**
     * 记录库存流水
     */
    public void record(Integer projectId, Integer materialId,
                       String bizType, Integer bizId, String bizNo,
                       int direction, BigDecimal quantity,
                       BigDecimal beforeQty, BigDecimal afterQty,
                       BigDecimal unitPrice) {
        BizInventoryRecord record = new BizInventoryRecord();
        record.setProjectId(projectId);
        record.setMaterialId(materialId);
        record.setBizType(bizType);
        record.setBizId(bizId);
        record.setBizNo(bizNo);
        record.setDirection(direction);
        record.setQuantity(quantity);
        record.setBeforeQuantity(beforeQty);
        record.setAfterQuantity(afterQty);
        record.setUnitPrice(unitPrice);
        record.setAmount(quantity.multiply(unitPrice != null ? unitPrice : BigDecimal.ZERO));
        record.setCreatedAt(LocalDateTime.now());
        recordMapper.insert(record);

        log.info("库存流水: project={}, material={}, type={}, direction={}, qty={}",
                projectId, materialId, bizType, direction, quantity);
    }
}
