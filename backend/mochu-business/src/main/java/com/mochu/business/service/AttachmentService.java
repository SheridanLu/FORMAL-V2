package com.mochu.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochu.business.entity.BizAttachment;
import com.mochu.business.entity.BizContract;
import com.mochu.business.entity.BizProject;
import com.mochu.business.entity.BizPurchaseList;
import com.mochu.business.entity.BizProjectMember;
import com.mochu.business.mapper.BizAttachmentMapper;
import com.mochu.business.mapper.BizContractMapper;
import com.mochu.business.mapper.BizProjectMapper;
import com.mochu.business.mapper.BizPurchaseListMapper;
import com.mochu.business.mapper.BizProjectMemberMapper;
import com.mochu.common.constant.Constants;
import com.mochu.common.exception.BusinessException;
import com.mochu.common.result.PageResult;
import com.mochu.common.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 附件服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final BizAttachmentMapper attachmentMapper;
    private final BizProjectMapper projectMapper;
    private final BizContractMapper contractMapper;
    private final BizPurchaseListMapper purchaseListMapper;
    private final BizProjectMemberMapper projectMemberMapper;
    private final MinioService minioService;

    /**
     * 上传附件
     */
    public BizAttachment upload(MultipartFile file, String bizType, Integer bizId) throws Exception {
        assertAttachmentAccess(bizType, bizId);
        String filePath = minioService.upload(file, bizType);

        String ext = "";
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
        }

        String md5 = DigestUtils.md5DigestAsHex(file.getInputStream());

        BizAttachment attachment = new BizAttachment();
        attachment.setFileName(originalFilename);
        attachment.setFilePath(filePath);
        attachment.setFileSize(file.getSize());
        attachment.setFileType(file.getContentType());
        attachment.setFileExt(ext);
        attachment.setMd5(md5);
        attachment.setBizType(bizType);
        attachment.setBizId(bizId);
        attachment.setStatus(1);
        attachment.setCreatorId(SecurityUtils.getCurrentUserId());
        attachmentMapper.insert(attachment);

        return attachment;
    }

    /**
     * 获取下载URL
     */
    public String getDownloadUrl(Integer id) throws Exception {
        BizAttachment attachment = getAccessibleAttachment(id);
        return minioService.getPresignedUrl(attachment.getFilePath());
    }

    /**
     * 查询业务附件列表
     */
    public List<BizAttachment> listByBiz(String bizType, Integer bizId) {
        assertAttachmentAccess(bizType, bizId);
        return attachmentMapper.selectList(
                new LambdaQueryWrapper<BizAttachment>()
                        .eq(BizAttachment::getBizType, bizType)
                        .eq(BizAttachment::getBizId, bizId)
                        .eq(BizAttachment::getStatus, 1)
                        .orderByDesc(BizAttachment::getCreatedAt));
    }

    /**
     * 分页查询附件
     */
    public PageResult<BizAttachment> list(String bizType, Integer bizId, Integer page, Integer size) {
        if (page == null || page < 1) page = Constants.DEFAULT_PAGE;
        if (size == null || size < 1) size = Constants.DEFAULT_SIZE;
        if (bizType != null && !bizType.isBlank() && bizId != null) {
            assertAttachmentAccess(bizType, bizId);
        }

        Page<BizAttachment> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<BizAttachment> wrapper = new LambdaQueryWrapper<>();

        if (bizType != null && !bizType.isBlank()) {
            wrapper.eq(BizAttachment::getBizType, bizType);
        }
        if (bizId != null) {
            wrapper.eq(BizAttachment::getBizId, bizId);
        }
        wrapper.eq(BizAttachment::getStatus, 1);
        wrapper.orderByDesc(BizAttachment::getCreatedAt);

        attachmentMapper.selectPage(pageParam, wrapper);
        return new PageResult<>(pageParam.getRecords(), pageParam.getTotal(), page, size);
    }

    /**
     * 删除附件
     */
    public void delete(Integer id) throws Exception {
        BizAttachment attachment = getAccessibleAttachment(id);
        minioService.delete(attachment.getFilePath());
        attachmentMapper.deleteById(id);
    }

    private BizAttachment getAccessibleAttachment(Integer id) {
        BizAttachment attachment = attachmentMapper.selectById(id);
        if (attachment == null) {
            throw new BusinessException("附件不存在");
        }
        assertAttachmentAccess(attachment.getBizType(), attachment.getBizId());
        return attachment;
    }

    private void assertAttachmentAccess(String bizType, Integer bizId) {
        Integer currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException("未登录或登录已过期");
        }
        boolean allowed = switch (bizType) {
            case "project" -> hasProjectAccess(bizId, currentUserId);
            case "contract" -> hasContractAccess(bizId, currentUserId);
            case "purchase", "purchase_list" -> hasPurchaseAccess(bizId, currentUserId);
            case "document" -> true; // 文档管理附件对所有登录用户开放
            default -> false;
        };
        if (!allowed) {
            throw new BusinessException("无权访问该业务附件");
        }
    }

    private boolean hasProjectAccess(Integer bizId, Integer currentUserId) {
        BizProject project = projectMapper.selectById(bizId);
        if (project == null) {
            return false;
        }
        if (currentUserId.equals(project.getCreatorId()) || currentUserId.equals(project.getManagerId())) {
            return true;
        }
        Long memberCount = projectMemberMapper.selectCount(
                new LambdaQueryWrapper<BizProjectMember>()
                        .eq(BizProjectMember::getProjectId, bizId)
                        .eq(BizProjectMember::getUserId, currentUserId));
        return memberCount != null && memberCount > 0;
    }

    private boolean hasContractAccess(Integer bizId, Integer currentUserId) {
        BizContract contract = contractMapper.selectById(bizId);
        if (contract == null || contract.getProjectId() == null) {
            return false;
        }
        return hasProjectAccess(contract.getProjectId(), currentUserId);
    }

    private boolean hasPurchaseAccess(Integer bizId, Integer currentUserId) {
        BizPurchaseList purchase = purchaseListMapper.selectById(bizId);
        if (purchase == null || purchase.getProjectId() == null) {
            return false;
        }
        return hasProjectAccess(purchase.getProjectId(), currentUserId);
    }
}
