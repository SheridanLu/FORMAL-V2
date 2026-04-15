package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 设备管理实体
 *
 * <p>V3.0: 支持设备台账、分配、保养提醒
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_equipment")
public class BizEquipment extends BaseEntity {

    /** 设备编号 */
    private String equipmentNo;

    /** 设备名称 */
    private String equipmentName;

    /** 规格型号 */
    private String model;

    /** 分类:owned/rented/subcontracted */
    private String category;

    /** 品牌 */
    private String brand;

    /** 出厂编号 */
    private String serialNo;

    /** 单位 */
    private String unit;

    /** 购置日期 */
    private LocalDate purchaseDate;

    /** 购置价格 */
    private BigDecimal purchasePrice;

    /** 供应商ID */
    private Integer supplierId;

    /** 当前所属项目ID */
    private Integer projectId;

    /** 当前存放位置 */
    private String location;

    /** 状态:idle/in_use/maintenance/scrapped */
    private String status;

    /** 上次保养日期 */
    private LocalDate lastMaintenanceDate;

    /** 下次保养日期 */
    private LocalDate nextMaintenanceDate;

    /** 备注 */
    private String remark;
}
