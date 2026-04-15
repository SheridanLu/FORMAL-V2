package com.mochu.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochu.business.entity.BizDocument;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 文档管理 Mapper
 */
@Mapper
public interface BizDocumentMapper extends BaseMapper<BizDocument> {

    /**
     * 分页查询(含项目名称 & 上传人姓名)
     */
    @Select("<script>"
            + "SELECT d.*, p.project_name, u.real_name AS uploader_name "
            + "FROM biz_document d "
            + "LEFT JOIN biz_project p ON d.project_id = p.id "
            + "LEFT JOIN sys_user u ON d.uploader_id = u.id "
            + "WHERE d.deleted = 0 "
            + "<if test='keyword != null and keyword != \"\"'>"
            + "  AND d.file_name LIKE CONCAT('%', #{keyword}, '%') "
            + "</if>"
            + "<if test='projectId != null'>"
            + "  AND d.project_id = #{projectId} "
            + "</if>"
            + "<if test='category != null and category != \"\"'>"
            + "  AND d.category = #{category} "
            + "</if>"
            + "ORDER BY d.created_at DESC"
            + "</script>")
    IPage<BizDocument> selectPageWithDetail(Page<BizDocument> page,
                                            @Param("keyword") String keyword,
                                            @Param("projectId") Integer projectId,
                                            @Param("category") String category);
}
