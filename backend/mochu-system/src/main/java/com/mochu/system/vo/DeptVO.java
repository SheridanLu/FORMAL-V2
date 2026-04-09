package com.mochu.system.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门 VO（树形结构）
 */
@Data
public class DeptVO {

    private Integer id;

    private String name;

    private Integer parentId;

    private Integer level;

    private String path;

    private Integer sort;

    private Integer leaderId;

    /** 负责人姓名（关联查询） */
    private String leaderName;

    private String phone;

    private String remark;

    private Integer status;

    private LocalDateTime createdAt;

    /** 子部门列表 */
    private List<DeptVO> children;
}
