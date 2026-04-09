package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 里程碑依赖关系
 */
@Data
@TableName("biz_milestone_dep")
public class BizMilestoneDep {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer milestoneId;

    private Integer depMilestoneId;

    private LocalDateTime createdAt;
}
