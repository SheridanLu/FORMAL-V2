package com.mochu.business.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TaxRateDTO {

    private Integer id;

    @NotNull(message = "税率级数不能为空")
    private Integer level;

    @NotNull(message = "最低收入不能为空")
    private BigDecimal minIncome;

    @NotNull(message = "最高收入不能为空")
    private BigDecimal maxIncome;

    @NotNull(message = "税率不能为空")
    private BigDecimal rate;

    @NotNull(message = "速算扣除数不能为空")
    private BigDecimal deduction;

    /* ---------- 查询条件 ---------- */

    private Integer page;

    private Integer size;
}
