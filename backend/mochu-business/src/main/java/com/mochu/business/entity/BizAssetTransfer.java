package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 资产交接表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_asset_transfer")
public class BizAssetTransfer extends BaseEntity {

    private String transferNo;

    private Integer userId;

    private String assetName;

    private String assetCode;

    private String transferType;

    private LocalDate transferDate;

    private String remark;

    private String status;
}
