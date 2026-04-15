package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 公司/单位信息实体
 *
 * <p>V3.0 §3.5: 支持合同/供应商表单的甲乙方信息自动填充
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_company_info")
public class BizCompanyInfo extends BaseEntity {

    /** 公司名称 */
    private String companyName;

    /** 统一社会信用代码 */
    private String creditCode;

    /** 法定代表人 */
    private String legalPerson;

    /** 注册资本(万元) */
    private BigDecimal registeredCapital;

    /** 注册地址 */
    private String address;

    /** 联系人 */
    private String contactPerson;

    /** 联系电话 */
    private String contactPhone;

    /** 开户银行 */
    private String bankName;

    /** 银行账号 */
    private String bankAccount;

    /** 税号 */
    private String taxNo;

    /** 备注 */
    private String remark;
}
