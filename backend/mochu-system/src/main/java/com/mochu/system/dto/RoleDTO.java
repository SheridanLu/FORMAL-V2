package com.mochu.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 角色 DTO
 */
@Data
public class RoleDTO {

    private Integer id;

    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    private Integer dataScope;

    private String remark;

    private Integer status;

    /** 权限ID列表 */
    private List<Integer> permissionIds;
}
