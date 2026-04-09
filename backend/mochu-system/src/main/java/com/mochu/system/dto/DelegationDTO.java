package com.mochu.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DelegationDTO {

    private Integer id;

    @NotNull(message = "被委托人不能为空")
    private Integer delegateeId;

    private List<String> permissionCodes;

    @NotNull(message = "生效时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "到期时间不能为空")
    private LocalDateTime endTime;

    private String remark;
}
