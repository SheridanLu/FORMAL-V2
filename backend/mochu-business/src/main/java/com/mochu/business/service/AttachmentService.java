package com.mochu.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochu.business.entity.BizAttachment;
import com.mochu.business.mapper.BizAttachmentMapper;
import com.mochu.common.constant.Constants;
import com.mochu.common.exception.BusinessException;
import com.mochu.common.result.PageResult;
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
    private final MinioService minioService;

    /**
     * 上传附件
     */
    public BizAttachment upload(MultipartFile file, String bizType, Integer bizId) throws Exception {
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
        attachmentMapper.insert(attachment);

        return attachment;
    }

    /**
     * 获取下载URL
     */
    public String getDownloadUrl(Integer id) throws Exception {
        BizAttachment attachment = attachmentMapper.selectById(id);
        if (attachment == null) {
            throw new BusinessException("附件不存在");
        }
        return minioService.getPresignedUrl(attachment.getFilePath());
    }

    /**
     * 查询业务附件列表
     */
    public List<BizAttachment> listByBiz(String bizType, Integer bizId) {
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
        BizAttachment attachment = attachmentMapper.selectById(id);
        if (attachment == null) return;
        minioService.delete(attachment.getFilePath());
        attachmentMapper.deleteById(id);
    }
}
