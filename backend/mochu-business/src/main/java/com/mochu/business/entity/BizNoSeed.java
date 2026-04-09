package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;

/**
 * 编号种子表 — 对照 V3.2 P.72
 * 复合主键: prefix + date_part
 */
@Data
@TableName("biz_no_seed")
public class BizNoSeed {

    private String prefix;

    private String datePart;

    private Integer currentSeq;
}
