package com.mochu.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;

@Data
@TableName("biz_contract_version")
public class BizContractVersion {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer contractId;
    private Integer versionNo;
    private String snapshotJson;
    private String changeSummary;
    private String changeType;
    private Integer creatorId;
    private LocalDateTime createdAt;
}
