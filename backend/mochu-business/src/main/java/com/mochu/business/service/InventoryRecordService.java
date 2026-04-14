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
     *
     * @param projectId  项目ID
     * @param materialId 物料ID
     * @param bizType    关联业务类型 (inbound/outbound/return/check_gain/check_loss/transfer)
     * @param bizId      关联业务ID
     * @param bizNo      关联业务单号（存入 remark）
     * @param direction  方向: 1=入, -1=出
     * @param quantity   变动数量（正数）
     * @param beforeQty  变动前数量
     * @param afterQty   变动后数量
     * @param unitPrice  单价（DDL 无此列，追加到 remark 保留审计信息）
     */
    public void record(Integer projectId, Integer materialId,
                       String bizType, Integer bizId, String bizNo,
                       int direction, BigDecimal quantity,
                       BigDecimal beforeQty, BigDecimal afterQty,
                       BigDecimal unitPrice) {
        BizInventoryRecord record = new BizInventoryRecord();
        record.setProjectId(projectId);
        record.setMaterialId(materialId);
        // DDL record_type 支持: inbound/outbound/return/check/transfer
        // 从 bizType 推导更精确的 recordType
        record.setRecordType(resolveRecordType(bizType, direction));
        record.setQuantity(direction > 0 ? quantity : quantity.negate());
        record.setBeforeQty(beforeQty);
        record.setAfterQty(afterQty);
        record.setBizType(bizType);
        record.setBizId(bizId);
        record.setRemark(buildRemark(bizNo, unitPrice));
        record.setCreatedAt(LocalDateTime.now());
        recordMapper.insert(record);

        log.info("库存流水: project={}, material={}, recordType={}, bizType={}, direction={}, qty={}",
                projectId, materialId, record.getRecordType(), bizType, direction, quantity);
    }

    /**
     * 从 bizType 推导 DDL 标准的 record_type
     * DDL 枚举: inbound / outbound / return / check / transfer
     */
    private String resolveRecordType(String bizType, int direction) {
        if (bizType == null) {
            return direction > 0 ? "inbound" : "outbound";
        }
        return switch (bizType) {
            case "inbound" -> "inbound";
            case "outbound" -> "outbound";
            case "return" -> "return";
            case "check_gain", "check_loss", "check" -> "check";
            case "transfer" -> "transfer";
            default -> direction > 0 ? "inbound" : "outbound";
        };
    }

    /**
     * 构建 remark —— 保留单号 + 单价审计信息（DDL 无 unit_price 列）
     */
    private String buildRemark(String bizNo, BigDecimal unitPrice) {
        StringBuilder sb = new StringBuilder();
        if (bizNo != null) {
            sb.append("单据: ").append(bizNo);
        }
        if (unitPrice != null && unitPrice.compareTo(BigDecimal.ZERO) > 0) {
            if (!sb.isEmpty()) sb.append("；");
            sb.append("单价: ").append(unitPrice.toPlainString());
        }
        return sb.isEmpty() ? null : sb.toString();
    }
}
