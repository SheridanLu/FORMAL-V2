package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 审批流程定义表 — 对照 V3.2 附录P.15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_flow_def")
public class SysFlowDef extends BaseEntity {

    /** 关联业务类型 */
    private String bizType;

    /** 流程名称 */
    private String flowName;

    /** 审批流程节点配置JSON */
    private String nodesJson;

    /** 条件分支配置JSON */
    private String conditionJson;

    /** 1启用/0禁用 */
    private Integer status;

    /** 流程版本号 */
    private Integer version;
}
