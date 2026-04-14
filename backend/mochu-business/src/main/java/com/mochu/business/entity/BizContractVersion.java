package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 合同版本历史表 — 对照 V3.2.0.03 DDL
 */
@Data
@TableName("biz_contract_version")
public class BizContractVersion {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer contractId;
    private Integer versionNo;
    private String snapshotJson;
    /** 变更原因 — DDL: change_reason */
    private String changeReason;
    /** 操作人ID — DDL: operator_id */
    private Integer operatorId;
    private LocalDateTime createdAt;
}
