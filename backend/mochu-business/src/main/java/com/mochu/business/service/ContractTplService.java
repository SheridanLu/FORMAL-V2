package com.mochu.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochu.business.dto.ContractTplDTO;
import com.mochu.business.dto.TplFieldUpdateDTO;
import com.mochu.business.entity.*;
import com.mochu.business.enums.ContractTypeEnum;
import com.mochu.business.mapper.*;
import com.mochu.common.constant.Constants;
import com.mochu.common.exception.BusinessException;
import com.mochu.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 合同模板管理服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractTplService {

    private final SysContractTplMapper tplMapper;
    private final SysContractTplVersionMapper versionMapper;
    private final SysContractTplFieldMapper fieldMapper;
    private final SysContractTplAuditMapper auditMapper;
    private final MinioService minioService;
    private final ApprovalService approvalService;

    /** 占位符正则: {{fieldKey}} 或 {{fieldKey:中文名}} */
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{([a-zA-Z_][a-zA-Z0-9_]*)(?::([^}]+))?}}");

    // ===================== 模板 CRUD =====================

    public PageResult<SysContractTpl> list(String contractType, Integer status, Integer page, Integer size) {
        int p = (page == null || page < 1) ? Constants.DEFAULT_PAGE : page;
        int s = (size == null || size < 1) ? Constants.DEFAULT_SIZE : size;

        Page<SysContractTpl> pageParam = new Page<>(p, s);
        LambdaQueryWrapper<SysContractTpl> wrapper = new LambdaQueryWrapper<>();
        if (contractType != null && !contractType.isBlank()) {
            wrapper.eq(SysContractTpl::getContractType, contractType);
        }
        if (status != null) {
            wrapper.eq(SysContractTpl::getStatus, status);
        }
        wrapper.orderByDesc(SysContractTpl::getId);
        tplMapper.selectPage(pageParam, wrapper);
        return new PageResult<>(pageParam.getRecords(), pageParam.getTotal(), p, s);
    }

    public SysContractTpl getById(Integer id) {
        return tplMapper.selectById(id);
    }

    @Transactional
    public Integer create(ContractTplDTO dto, Integer operatorId) {
        if (!ContractTypeEnum.isValid(dto.getContractType())) {
            throw new BusinessException("无效的合同类型");
        }
        // 检查该类型是否已有模板
        Long count = tplMapper.selectCount(
                new LambdaQueryWrapper<SysContractTpl>()
                        .eq(SysContractTpl::getContractType, dto.getContractType()));
        if (count > 0) {
            throw new BusinessException("该合同类型已存在模板，不可重复创建");
        }

        SysContractTpl entity = new SysContractTpl();
        BeanUtils.copyProperties(dto, entity);
        if (entity.getStatus() == null) entity.setStatus(1);
        entity.setCreatorId(operatorId);
        entity.setCreatedAt(LocalDateTime.now());
        tplMapper.insert(entity);

        writeAudit(entity.getId(), null, "create", "创建模板: " + entity.getTplName(), operatorId);
        return entity.getId();
    }

    @Transactional
    public void update(Integer id, ContractTplDTO dto, Integer operatorId) {
        SysContractTpl entity = tplMapper.selectById(id);
        if (entity == null) throw new BusinessException("模板不存在");

        entity.setTplName(dto.getTplName());
        entity.setDescription(dto.getDescription());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        tplMapper.updateById(entity);

        writeAudit(id, null, "update", "更新模板信息", operatorId);
    }

    @Transactional
    public void delete(Integer id, Integer operatorId) {
        SysContractTpl entity = tplMapper.selectById(id);
        if (entity == null) throw new BusinessException("模板不存在");
        tplMapper.deleteById(id);
        writeAudit(id, null, "delete", "删除模板: " + entity.getTplName(), operatorId);
    }

    // ===================== 版本管理 =====================

    /**
     * 上传新版本（解析占位符字段 + 停用旧版本）
     */
    @Transactional
    public SysContractTplVersion uploadVersion(Integer tplId, MultipartFile file, Integer operatorId) {
        SysContractTpl tpl = tplMapper.selectById(tplId);
        if (tpl == null) throw new BusinessException("模板不存在");

        try {
            // 上传文件到 MinIO
            String filePath = minioService.upload(file, "contract-tpl");
            String md5 = DigestUtils.md5DigestAsHex(file.getInputStream());

            // 计算新版本号
            Integer maxVersion = versionMapper.selectList(
                    new LambdaQueryWrapper<SysContractTplVersion>()
                            .eq(SysContractTplVersion::getTplId, tplId)
                            .orderByDesc(SysContractTplVersion::getVersionNo)
                            .last("LIMIT 1")
            ).stream().findFirst().map(SysContractTplVersion::getVersionNo).orElse(0);

            // 停用旧的启用版本
            versionMapper.update(null,
                    new LambdaUpdateWrapper<SysContractTplVersion>()
                            .eq(SysContractTplVersion::getTplId, tplId)
                            .eq(SysContractTplVersion::getStatus, 1)
                            .set(SysContractTplVersion::getStatus, 0));

            // 创建新版本
            SysContractTplVersion version = new SysContractTplVersion();
            version.setTplId(tplId);
            version.setVersionNo(maxVersion + 1);
            version.setFilePath(filePath);
            version.setFileName(file.getOriginalFilename());
            version.setFileMd5(md5);
            version.setStatus(1);
            version.setCreatorId(operatorId);
            version.setCreatedAt(LocalDateTime.now());

            // 解析文件内容中的占位符
            String content = new String(file.getBytes());
            version.setHtmlCache(content); // 简化：存储原始内容用于预览
            versionMapper.insert(version);

            // 从内容中解析占位符并创建字段定义
            List<SysContractTplField> fields = parsePlaceholders(content, version.getId());
            for (SysContractTplField field : fields) {
                fieldMapper.insert(field);
            }

            writeAudit(tplId, version.getId(), "upload",
                    "上传新版本V" + version.getVersionNo() + ": " + file.getOriginalFilename(), operatorId);

            return version;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("模板上传失败: " + e.getMessage());
        }
    }

    public SysContractTplVersion getVersionById(Integer versionId) {
        return versionMapper.selectById(versionId);
    }

    public List<SysContractTplVersion> listVersions(Integer tplId) {
        return versionMapper.selectList(
                new LambdaQueryWrapper<SysContractTplVersion>()
                        .eq(SysContractTplVersion::getTplId, tplId)
                        .orderByDesc(SysContractTplVersion::getVersionNo));
    }

    /**
     * 启用/停用版本
     */
    @Transactional
    public void updateVersionStatus(Integer versionId, Integer status, Integer operatorId) {
        SysContractTplVersion version = versionMapper.selectById(versionId);
        if (version == null) throw new BusinessException("版本不存在");

        if (status == 1) {
            // 启用新版本时，停用同模板的其他启用版本
            versionMapper.update(null,
                    new LambdaUpdateWrapper<SysContractTplVersion>()
                            .eq(SysContractTplVersion::getTplId, version.getTplId())
                            .eq(SysContractTplVersion::getStatus, 1)
                            .set(SysContractTplVersion::getStatus, 0));
        }

        version.setStatus(status);
        versionMapper.updateById(version);

        String action = status == 1 ? "enable" : "disable";
        writeAudit(version.getTplId(), versionId, action,
                (status == 1 ? "启用" : "停用") + "版本V" + version.getVersionNo(), operatorId);
    }

    /**
     * 获取模板预览 HTML
     */
    public String getPreviewHtml(Integer versionId) {
        SysContractTplVersion version = versionMapper.selectById(versionId);
        if (version == null) throw new BusinessException("版本不存在");
        return version.getHtmlCache();
    }

    /**
     * 获取模板下载 URL
     */
    public String getDownloadUrl(Integer versionId) {
        SysContractTplVersion version = versionMapper.selectById(versionId);
        if (version == null) throw new BusinessException("版本不存在");
        try {
            return minioService.getPresignedUrl(version.getFilePath());
        } catch (Exception e) {
            throw new BusinessException("获取下载链接失败");
        }
    }

    // ===================== 字段管理 =====================

    public List<SysContractTplField> listFields(Integer versionId) {
        return fieldMapper.selectList(
                new LambdaQueryWrapper<SysContractTplField>()
                        .eq(SysContractTplField::getVersionId, versionId)
                        .orderByAsc(SysContractTplField::getSortOrder));
    }

    @Transactional
    public void updateFields(Integer versionId, TplFieldUpdateDTO dto, Integer operatorId) {
        SysContractTplVersion version = versionMapper.selectById(versionId);
        if (version == null) throw new BusinessException("版本不存在");

        // 删除旧字段定义，重建
        fieldMapper.delete(
                new LambdaQueryWrapper<SysContractTplField>()
                        .eq(SysContractTplField::getVersionId, versionId));

        int order = 0;
        for (TplFieldUpdateDTO.FieldItem item : dto.getFields()) {
            SysContractTplField field = new SysContractTplField();
            field.setVersionId(versionId);
            field.setFieldKey(item.getFieldKey());
            field.setFieldName(item.getFieldName());
            field.setFieldType(item.getFieldType() != null ? item.getFieldType() : "text");
            field.setRequired(item.getRequired() != null ? item.getRequired() : 0);
            field.setOptionsJson(item.getOptionsJson());
            field.setDefaultValue(item.getDefaultValue());
            field.setSortOrder(item.getSortOrder() != null ? item.getSortOrder() : order++);
            field.setPlaceholder(item.getPlaceholder());
            field.setMaxLength(item.getMaxLength());
            field.setValidationRule(item.getValidationRule());
            fieldMapper.insert(field);
        }

        writeAudit(version.getTplId(), versionId, "update_fields",
                "更新字段定义(" + dto.getFields().size() + "个字段)", operatorId);
    }

    // ===================== 审批集成 =====================

    /**
     * 提交版本启用审批（电子流审批）
     * 有审批流时提交审批，无审批流时直接启用
     */
    @Transactional
    public void submitVersionApproval(Integer versionId, Integer operatorId) {
        SysContractTplVersion version = versionMapper.selectById(versionId);
        if (version == null) throw new BusinessException("版本不存在");

        boolean hasFlow = approvalService.hasFlowDef("contract_tpl");
        if (hasFlow) {
            approvalService.submitForApproval("contract_tpl", versionId, operatorId);
            writeAudit(version.getTplId(), versionId, "submit_approval",
                    "提交版本V" + version.getVersionNo() + "启用审批", operatorId);
        } else {
            // 无审批流，直接启用
            updateVersionStatus(versionId, 1, operatorId);
        }
    }

    // ===================== 审计日志 =====================

    public PageResult<SysContractTplAudit> listAuditLogs(Integer tplId, Integer page, Integer size) {
        int p = (page == null || page < 1) ? Constants.DEFAULT_PAGE : page;
        int s = (size == null || size < 1) ? Constants.DEFAULT_SIZE : size;

        Page<SysContractTplAudit> pageParam = new Page<>(p, s);
        auditMapper.selectPage(pageParam,
                new LambdaQueryWrapper<SysContractTplAudit>()
                        .eq(SysContractTplAudit::getTplId, tplId)
                        .orderByDesc(SysContractTplAudit::getOperatedAt));
        return new PageResult<>(pageParam.getRecords(), pageParam.getTotal(), p, s);
    }

    // ===================== 公开查询（供合同创建使用） =====================

    /**
     * 获取指定合同类型当前启用且在有效期内的模板版本
     */
    public SysContractTplVersion getActiveVersion(String contractType) {
        SysContractTpl tpl = tplMapper.selectOne(
                new LambdaQueryWrapper<SysContractTpl>()
                        .eq(SysContractTpl::getContractType, contractType)
                        .eq(SysContractTpl::getStatus, 1));
        if (tpl == null) return null;

        LocalDateTime now = LocalDateTime.now();
        return versionMapper.selectOne(
                new LambdaQueryWrapper<SysContractTplVersion>()
                        .eq(SysContractTplVersion::getTplId, tpl.getId())
                        .eq(SysContractTplVersion::getStatus, 1)
                        .and(w -> w
                                .isNull(SysContractTplVersion::getEffectiveFrom)
                                .or().le(SysContractTplVersion::getEffectiveFrom, now))
                        .and(w -> w
                                .isNull(SysContractTplVersion::getEffectiveUntil)
                                .or().ge(SysContractTplVersion::getEffectiveUntil, now))
                        .orderByDesc(SysContractTplVersion::getVersionNo)
                        .last("LIMIT 1"));
    }

    // ===================== 内部方法 =====================

    /**
     * 从模板内容中解析 {{placeholder}} 或 {{placeholder:中文名}} 占位符
     */
    private List<SysContractTplField> parsePlaceholders(String content, Integer versionId) {
        List<SysContractTplField> fields = new ArrayList<>();
        java.util.Set<String> seen = new java.util.HashSet<>();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(content);
        int order = 0;

        while (matcher.find()) {
            String key = matcher.group(1);
            String name = matcher.group(2);
            if (seen.contains(key)) continue;
            seen.add(key);

            SysContractTplField field = new SysContractTplField();
            field.setVersionId(versionId);
            field.setFieldKey(key);
            field.setFieldName(name != null ? name : key);
            field.setFieldType("text");
            field.setRequired(0);
            field.setSortOrder(order++);
            fields.add(field);
        }
        return fields;
    }

    private void writeAudit(Integer tplId, Integer versionId, String action, String detail, Integer operatorId) {
        SysContractTplAudit audit = new SysContractTplAudit();
        audit.setTplId(tplId);
        audit.setVersionId(versionId);
        audit.setAction(action);
        audit.setDetail(detail);
        audit.setOperatorId(operatorId);
        audit.setOperatedAt(LocalDateTime.now());
        auditMapper.insert(audit);
    }
}
