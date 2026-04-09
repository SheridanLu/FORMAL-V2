package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_external_contact")
public class BizExternalContact extends BaseEntity {

    private String name;

    private String company;

    private String phone;

    private String email;

    private String contactType;

    private String position;

    private String remark;
}
