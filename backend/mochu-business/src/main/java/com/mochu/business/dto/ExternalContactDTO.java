package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExternalContactDTO {

    private Integer id;

    @NotBlank(message = "联系人姓名不能为空")
    private String name;

    private String company;

    private String phone;

    private String email;

    private String contactType;

    private String position;

    private String remark;
}
