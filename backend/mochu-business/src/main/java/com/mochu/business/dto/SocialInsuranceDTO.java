package com.mochu.business.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SocialInsuranceDTO {

    private Integer id;

    @NotNull(message = "员工ID不能为空")
    private Integer userId;

    private BigDecimal pensionBase;

    private BigDecimal medicalBase;

    private BigDecimal unemploymentBase;

    private BigDecimal injuryBase;

    private BigDecimal maternityBase;

    private BigDecimal housingBase;

    private String remark;

    /* ---------- 查询条件 ---------- */

    private Integer page;

    private Integer size;

    private String status;
}
