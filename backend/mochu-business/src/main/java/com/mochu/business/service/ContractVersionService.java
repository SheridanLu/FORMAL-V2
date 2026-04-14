package com.mochu.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mochu.business.entity.BizContract;
import com.mochu.business.entity.BizContractVersion;
import com.mochu.business.mapper.BizContractVersionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractVersionService {

    private final BizContractVersionMapper versionMapper;
    private final ObjectMapper objectMapper;

    /**
     * 生成版本快照
     * #N7 fix: 加 @Transactional 保证版本号查询与插入的原子性
     * @param contract    合同实体
     * @param changeType  变更类型: supplement/terminate/amend
     * @param summary     变更摘要
     * @param operatorId  操作人ID
     */
    @Transactional
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
            version.setChangeReason(buildChangeReason(changeType, summary));
            version.setOperatorId(operatorId);

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
     * #N7 fix: 获取当前最大版本号 — 使用 FOR UPDATE 防止并发版本号冲突
     */
    private Integer getMaxVersionNo(Integer contractId) {
        BizContractVersion latest = versionMapper.selectOne(
                new LambdaQueryWrapper<BizContractVersion>()
                        .eq(BizContractVersion::getContractId, contractId)
                        .orderByDesc(BizContractVersion::getVersionNo)
                        .last("LIMIT 1 FOR UPDATE"));
        return latest != null ? latest.getVersionNo() : null;
    }

    /** changeType 中文映射表 */
    private static final Map<String, String> CHANGE_TYPE_LABELS = Map.of(
            "supplement", "补充协议",
            "terminate",  "合同终止",
            "amend",      "合同变更",
            "renew",      "合同续签"
    );

    /**
     * 构建变更原因 — summary 优先；无 summary 时用 changeType 的中文标签兜底
     */
    private String buildChangeReason(String changeType, String summary) {
        if (summary != null && !summary.isBlank()) return summary;
        return CHANGE_TYPE_LABELS.getOrDefault(changeType, changeType);
    }
}
