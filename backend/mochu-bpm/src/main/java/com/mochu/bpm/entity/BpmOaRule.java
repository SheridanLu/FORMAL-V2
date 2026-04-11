package com.mochu.bpm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bpm_oa_rule")
public class BpmOaRule extends BaseEntity {
    private String bizType;
    private String bizName;
    private String processDefKey;
    private Integer enabled;
}
