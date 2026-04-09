package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 合同模板操作审计日志
 */
@Data
@TableName("sys_contract_tpl_audit")
public class SysContractTplAudit {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer tplId;
    private Integer versionId;
    private String action;
    private String detail;
    private Integer operatorId;
    private LocalDateTime operatedAt;
}
