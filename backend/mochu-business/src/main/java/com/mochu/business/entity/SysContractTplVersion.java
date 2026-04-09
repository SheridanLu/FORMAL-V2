package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 合同模板版本表
 */
@Data
@TableName("sys_contract_tpl_version")
public class SysContractTplVersion {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer tplId;
    private Integer versionNo;
    private String filePath;
    private String fileName;
    private String fileMd5;
    private String htmlCache;
    private Integer status;
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveUntil;
    private Integer creatorId;
    private LocalDateTime createdAt;
}
