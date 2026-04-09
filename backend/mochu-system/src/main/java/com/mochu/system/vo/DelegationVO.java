package com.mochu.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DelegationVO {

    private Integer id;

    private Integer delegatorId;

    private String delegatorName;

    private Integer delegateeId;

    private String delegateeName;

    private String permissionCodes;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String remark;

    private Integer status;

    private LocalDateTime createdAt;
}
