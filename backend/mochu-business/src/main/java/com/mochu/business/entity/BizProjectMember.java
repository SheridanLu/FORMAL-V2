package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mochu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_project_member")
public class BizProjectMember extends BaseEntity {
    private Integer projectId;
    private Integer userId;
    private String role;
    private LocalDate joinDate;
}
