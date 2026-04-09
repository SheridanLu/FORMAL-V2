package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 合同模板主表
 */
@Data
@TableName("sys_contract_tpl")
public class SysContractTpl {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String contractType;
    private String tplName;
    private String description;
    private Integer status;
    private Integer creatorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}
