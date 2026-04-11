package com.mochu.bpm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bpm_process_definition_ext")
public class BpmProcessDefinitionExt extends BaseEntity {
    private String processDefKey;
    private String processDefId;
    private String bizType;
    private String formConfig;
    private Integer candidateStrategy;
    private String candidateParam;
    private Integer status;
    private String remark;
}
