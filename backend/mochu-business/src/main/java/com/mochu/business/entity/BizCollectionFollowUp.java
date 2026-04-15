package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 回款跟进记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_collection_follow_up")
public class BizCollectionFollowUp extends BaseEntity {

    private Integer receiptPlanId;
    private LocalDate followUpDate;
    private String result;
    private String nextAction;
    private Integer handlerId;
}
