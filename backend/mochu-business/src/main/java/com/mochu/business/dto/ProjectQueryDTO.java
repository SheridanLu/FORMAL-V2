package com.mochu.business.dto;

import lombok.Data;

@Data
public class ProjectQueryDTO {

    private String projectName;

    private String projectNo;

    private Integer projectType;

    private String status;

    private Integer managerId;

    private Integer page;

    private Integer size;
}
