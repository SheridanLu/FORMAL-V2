package com.mochu.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mochu.business.entity.BizContract;
import com.mochu.business.entity.BizContractVersion;
import com.mochu.business.mapper.BizContractVersionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractVersionService {

    private final BizContractVersionMapper versionMapper;
    private final ObjectMapper objectMapper;

    /**
     * 生成版本快照
     * @param contract    合同实体
     * @param changeType  变更类型: supplement/terminate/amend
     * @param summary     变更摘要
     * @param operatorId  操作人ID
     */
    public void createSnapshot(BizContract contract, String changeType,
                                String summary, Integer operatorId) {
        try {
            // 获取当前最大版本号
            Integer maxVersion = getMaxVersionNo(contract.getId());
            int newVersionNo = (maxVersion == null) ? 1 : maxVersion + 1;

            BizContractVersion version = new BizContractVersion();
            version.setContractId(contract.getId());
            version.setVersionNo(newVersionNo);
            version.setSnapshotJson(objectMapper.writeValueAsString(contract));
            version.setChangeSummary(summary);
            version.setChangeType(changeType);
            version.setCreatorId(operatorId);

            versionMapper.insert(version);
            log.info("合同版本快照已创建: contractId={}, version={}, type={}",
                    contract.getId(), newVersionNo, changeType);
        } catch (Exception e) {
            log.error("合同版本快照创建失败: contractId={}", contract.getId(), e);
            // 快照失败不阻塞主流程
        }
    }

    /**
     * 获取合同的所有版本
     */
    public List<BizContractVersion> getVersions(Integer contractId) {
        return versionMapper.selectList(
                new LambdaQueryWrapper<BizContractVersion>()
                        .eq(BizContractVersion::getContractId, contractId)
                        .orderByDesc(BizContractVersion::getVersionNo));
    }

    /**
     * 获取当前最大版本号
     */
    private Integer getMaxVersionNo(Integer contractId) {
        BizContractVersion latest = versionMapper.selectOne(
                new LambdaQueryWrapper<BizContractVersion>()
                        .eq(BizContractVersion::getContractId, contractId)
                        .orderByDesc(BizContractVersion::getVersionNo)
                        .last("LIMIT 1"));
        return latest != null ? latest.getVersionNo() : null;
    }
}
