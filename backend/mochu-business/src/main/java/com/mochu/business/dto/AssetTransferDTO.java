package com.mochu.business.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AssetTransferDTO {

    private Integer id;

    @NotNull(message = "员工ID不能为空")
    private Integer userId;

    @NotNull(message = "资产名称不能为空")
    private String assetName;

    private String assetCode;

    @NotNull(message = "交接类型不能为空")
    private String transferType;

    private LocalDate transferDate;

    private String remark;

    /* ---------- 查询条件 ---------- */

    private Integer page;

    private Integer size;

    private String status;
}
