package com.mochu.business.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectMemberDTO {

    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    private String role;
}
