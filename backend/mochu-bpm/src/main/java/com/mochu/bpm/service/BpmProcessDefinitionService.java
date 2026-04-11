package com.mochu.bpm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mochu.bpm.dto.ProcessDeployDTO;
import com.mochu.bpm.entity.BpmProcessDefinitionExt;
import com.mochu.bpm.mapper.BpmProcessDefinitionExtMapper;
import com.mochu.bpm.vo.ProcessDefinitionVO;
import com.mochu.common.exception.BusinessException;
import com.mochu.common.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程定义管理服务
 */
@Service
@RequiredArgsConstructor
public class BpmProcessDefinitionService {

    private final RepositoryService repositoryService;
    private final BpmProcessDefinitionExtMapper extMapper;

    /**
     * 查询所有已部署流程定义（最新版本）
     */
    public List<ProcessDefinitionVO> listProcessDefinitions(String keyword) {
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery()
                .latestVersion()
                .orderByProcessDefinitionKey().asc();
        if (keyword != null && !keyword.isBlank()) {
            query.processDefinitionNameLike("%" + keyword + "%");
        }
        List<ProcessDefinition> defs = query.list();

        // 批量查扩展信息
        List<String> defIds = defs.stream().map(ProcessDefinition::getId).collect(Collectors.toList());
        Map<String, BpmProcessDefinitionExt> extMap = extMapper.selectList(
                new LambdaQueryWrapper<BpmProcessDefinitionExt>()
                        .in(BpmProcessDefinitionExt::getProcessDefId, defIds))
                .stream().collect(Collectors.toMap(BpmProcessDefinitionExt::getProcessDefId, e -> e));

        return defs.stream().map(d -> toVO(d, extMap.get(d.getId()))).collect(Collectors.toList());
    }

    /**
     * 部署流程（BPMN XML字符串）
     */
    @Transactional
    public String deployProcess(ProcessDeployDTO dto) {
        Deployment deployment = repositoryService.createDeployment()
                .name(dto.getProcessDefKey())
                .addString(dto.getProcessDefKey() + ".bpmn20.xml", dto.getBpmn20Xml())
                .deploy();

        ProcessDefinition def = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();

        // 保存扩展信息
        BpmProcessDefinitionExt ext = new BpmProcessDefinitionExt();
        ext.setProcessDefKey(dto.getProcessDefKey());
        ext.setProcessDefId(def.getId());
        ext.setBizType(dto.getBizType() != null ? dto.getBizType() : "");
        ext.setFormConfig(dto.getFormConfig());
        ext.setCandidateStrategy(dto.getCandidateStrategy() != null ? dto.getCandidateStrategy() : 1);
        ext.setCandidateParam(dto.getCandidateParam() != null ? dto.getCandidateParam() : "");
        ext.setRemark(dto.getRemark() != null ? dto.getRemark() : "");
        ext.setStatus(1);
        ext.setCreatorId(SecurityUtils.getCurrentUserId());
        extMapper.insert(ext);

        return def.getId();
    }

    /**
     * 获取流程 BPMN XML
     */
    public String getProcessBpmnXml(String processDefId) {
        try (InputStream is = repositoryService.getResourceAsStream(
                repositoryService.createProcessDefinitionQuery()
                        .processDefinitionId(processDefId)
                        .singleResult().getDeploymentId(),
                repositoryService.createProcessDefinitionQuery()
                        .processDefinitionId(processDefId)
                        .singleResult().getResourceName())) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new BusinessException("获取流程XML失败");
        }
    }

    /**
     * 挂起/激活流程定义
     */
    public void suspendOrActivate(String processDefId, boolean suspend) {
        if (suspend) {
            repositoryService.suspendProcessDefinitionById(processDefId, true, null);
        } else {
            repositoryService.activateProcessDefinitionById(processDefId, true, null);
        }
    }

    /**
     * 删除流程部署（级联删除运行中实例）
     */
    @Transactional
    public void deleteDeployment(String processDefId) {
        ProcessDefinition def = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefId).singleResult();
        if (def == null) throw new BusinessException("流程定义不存在");
        repositoryService.deleteDeployment(def.getDeploymentId(), true);
        extMapper.delete(new LambdaQueryWrapper<BpmProcessDefinitionExt>()
                .eq(BpmProcessDefinitionExt::getProcessDefId, processDefId));
    }

    /**
     * 更新扩展配置
     */
    public void updateExt(String processDefId, ProcessDeployDTO dto) {
        BpmProcessDefinitionExt ext = extMapper.selectOne(
                new LambdaQueryWrapper<BpmProcessDefinitionExt>()
                        .eq(BpmProcessDefinitionExt::getProcessDefId, processDefId));
        if (ext == null) throw new BusinessException("扩展配置不存在");
        if (dto.getBizType() != null) ext.setBizType(dto.getBizType());
        if (dto.getCandidateStrategy() != null) ext.setCandidateStrategy(dto.getCandidateStrategy());
        if (dto.getCandidateParam() != null) ext.setCandidateParam(dto.getCandidateParam());
        if (dto.getFormConfig() != null) ext.setFormConfig(dto.getFormConfig());
        if (dto.getRemark() != null) ext.setRemark(dto.getRemark());
        extMapper.updateById(ext);
    }

    private ProcessDefinitionVO toVO(ProcessDefinition d, BpmProcessDefinitionExt ext) {
        ProcessDefinitionVO vo = new ProcessDefinitionVO();
        vo.setId(d.getId());
        vo.setKey(d.getKey());
        vo.setName(d.getName());
        vo.setVersion(d.getVersion());
        vo.setCategory(d.getCategory());
        vo.setDeploymentId(d.getDeploymentId());
        vo.setSuspended(d.isSuspended());
        if (ext != null) {
            vo.setBizType(ext.getBizType());
            vo.setCandidateStrategy(ext.getCandidateStrategy());
            vo.setCandidateParam(ext.getCandidateParam());
            vo.setFormConfig(ext.getFormConfig());
            vo.setRemark(ext.getRemark());
            vo.setExtStatus(ext.getStatus());
        }
        return vo;
    }
}
