package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("biz_income_split_item")
public class BizIncomeSplitItem {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer splitId;
    private String taskName;
    private BigDecimal amount;
    private BigDecimal ratio;
    private String remark;
    private LocalDateTime createdAt;
}
