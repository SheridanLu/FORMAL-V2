package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 设备管理 DTO
 */
@Data
public class EquipmentDTO {

    @NotBlank(message = "设备名称不能为空")
    @Size(max = 200, message = "设备名称最长200字符")
    private String equipmentName;

    @Size(max = 100, message = "规格型号最长100字符")
    private String model;

    private String category;

    @Size(max = 100, message = "品牌最长100字符")
    private String brand;

    @Size(max = 100, message = "出厂编号最长100字符")
    private String serialNo;

    @Size(max = 20, message = "单位最长20字符")
    private String unit;

    private LocalDate purchaseDate;

    private BigDecimal purchasePrice;

    private Integer supplierId;

    private Integer projectId;

    @Size(max = 200, message = "位置最长200字符")
    private String location;

    private String status;

    private LocalDate lastMaintenanceDate;

    private LocalDate nextMaintenanceDate;

    @Size(max = 500, message = "备注最长500字符")
    private String remark;
}
