package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 社保配置表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_social_insurance")
public class BizSocialInsurance extends BaseEntity {

    private Integer userId;

    private BigDecimal pensionBase;

    private BigDecimal medicalBase;

    private BigDecimal unemploymentBase;

    private BigDecimal injuryBase;

    private BigDecimal maternityBase;

    private BigDecimal housingBase;

    private String remark;

    private String status;
}
