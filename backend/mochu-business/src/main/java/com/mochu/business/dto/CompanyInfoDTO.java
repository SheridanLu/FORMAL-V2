package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 公司信息 DTO
 */
@Data
public class CompanyInfoDTO {

    @NotBlank(message = "公司名称不能为空")
    @Size(max = 200, message = "公司名称最长200字符")
    private String companyName;

    @Size(max = 50, message = "统一社会信用代码最长50字符")
    private String creditCode;

    @Size(max = 50, message = "法定代表人最长50字符")
    private String legalPerson;

    private BigDecimal registeredCapital;

    @Size(max = 500, message = "地址最长500字符")
    private String address;

    @Size(max = 50, message = "联系人最长50字符")
    private String contactPerson;

    @Size(max = 30, message = "联系电话最长30字符")
    private String contactPhone;

    @Size(max = 200, message = "开户银行最长200字符")
    private String bankName;

    @Size(max = 50, message = "银行账号最长50字符")
    private String bankAccount;

    @Size(max = 50, message = "税号最长50字符")
    private String taxNo;

    @Size(max = 500, message = "备注最长500字符")
    private String remark;
}
