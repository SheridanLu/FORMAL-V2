package com.mochu.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建/更新部门请求
 */
@Data
public class DeptDTO {

    private Integer id;

    @NotBlank(message = "部门名称不能为空")
    private String name;

    @NotNull(message = "上级部门不能为空")
    private Integer parentId;

    private Integer sort;

    private Integer leaderId;

    private String phone;

    private String remark;

    private Integer status;
}
