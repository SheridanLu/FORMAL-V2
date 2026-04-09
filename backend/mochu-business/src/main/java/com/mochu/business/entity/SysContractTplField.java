package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 合同模板字段定义表
 */
@Data
@TableName("sys_contract_tpl_field")
public class SysContractTplField {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer versionId;
    private String fieldKey;
    private String fieldName;
    private String fieldType;
    private Integer required;
    private String optionsJson;
    private String defaultValue;
    private Integer sortOrder;
    private String placeholder;
    private Integer maxLength;
    private String validationRule;
}
