package com.mochu.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochu.business.dto.DocumentDTO;
import com.mochu.business.entity.BizDocument;
import com.mochu.business.mapper.BizDocumentMapper;
import com.mochu.common.exception.BusinessException;
import com.mochu.common.result.PageResult;
import com.mochu.common.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文档管理 Service
 *
 * <p>V3.2: 文档上传、分类查询、版本追踪、删除
 */
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final BizDocumentMapper documentMapper;
    private final AttachmentService attachmentService;

    /**
     * 分页查询（联表获取项目名称 & 上传人姓名）
     */
    public PageResult<BizDocument> list(String keyword, Integer projectId,
                                        String category, int page, int size) {
        Page<BizDocument> pageParam = new Page<>(page, size);
        documentMapper.selectPageWithDetail(pageParam, keyword, projectId, category);
        return new PageResult<>(pageParam.getRecords(), pageParam.getTotal(), page, size);
    }

    /**
     * 详情
     */
    public BizDocument getById(Integer id) {
        return documentMapper.selectById(id);
    }

    /**
     * 文件上传并创建文档记录
     *
     * @param file      上传的文件
     * @param category  分类
     * @param projectId 关联项目ID（可选）
     */
    @Transactional
    public BizDocument upload(MultipartFile file, String category, Integer projectId) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 通过附件服务完成实际文件存储（安全校验 + MD5秒传 + MinIO上传）
        var attachment = attachmentService.upload(file, "document", null);

        BizDocument entity = new BizDocument();
        entity.setFileName(file.getOriginalFilename());
        entity.setFileUrl(attachment.getFilePath());
        entity.setFilePath(attachment.getFilePath());
        entity.setFileSize(file.getSize());
        entity.setFileType(file.getContentType());
        entity.setCategory(StringUtils.hasText(category) ? category : "other");
        entity.setProjectId(projectId);
        entity.setUploaderId(SecurityUtils.getCurrentUserId());
        entity.setVersion(1);
        documentMapper.insert(entity);
        return entity;
    }

    /**
     * 手动创建文档记录（不上传文件，引用已有附件）
     */
    public void create(DocumentDTO dto) {
        BizDocument entity = new BizDocument();
        BeanUtils.copyProperties(dto, entity);
        entity.setUploaderId(SecurityUtils.getCurrentUserId());
        entity.setVersion(1);
        documentMapper.insert(entity);
    }

    /**
     * 更新文档元信息
     */
    public void update(Integer id, DocumentDTO dto) {
        BizDocument entity = documentMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("文档不存在");
        }
        BeanUtils.copyProperties(dto, entity, "id", "uploaderId", "version");
        documentMapper.updateById(entity);
    }

    /**
     * 删除文档
     */
    public void delete(Integer id) {
        BizDocument entity = documentMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("文档不存在");
        }
        documentMapper.deleteById(id);
    }
}
