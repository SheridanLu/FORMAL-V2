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
     * 记录库存流水 — 对照 V3.2.0.08 DDL: biz_inventory_record
     */
    public void record(Integer projectId, Integer materialId,
                       String bizType, Integer bizId, String bizNo,
                       int direction, BigDecimal quantity,
                       BigDecimal beforeQty, BigDecimal afterQty,
                       BigDecimal unitPrice) {
        BizInventoryRecord record = new BizInventoryRecord();
        record.setProjectId(projectId);
        record.setMaterialId(materialId);
        // DDL uses record_type for direction-based type (inbound/outbound/return/check/transfer)
        record.setRecordType(direction > 0 ? "inbound" : "outbound");
        record.setQuantity(direction > 0 ? quantity : quantity.negate());
        record.setBeforeQty(beforeQty);
        record.setAfterQty(afterQty);
        record.setBizType(bizType);
        record.setBizId(bizId);
        record.setRemark(bizNo != null ? "单据: " + bizNo : null);
        record.setCreatedAt(LocalDateTime.now());
        recordMapper.insert(record);

        log.info("库存流水: project={}, material={}, type={}, direction={}, qty={}",
                projectId, materialId, bizType, direction, quantity);
    }
}
