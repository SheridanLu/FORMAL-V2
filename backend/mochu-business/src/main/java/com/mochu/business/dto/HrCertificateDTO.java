package com.mochu.business.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HrCertificateDTO {

    private Integer id;

    @NotBlank(message = "资质类型不能为空")
    private String certType;

    private Integer userId;

    @NotBlank(message = "证书名称不能为空")
    private String certName;

    private String certCategory;

    private String certNo;

    private LocalDate issueDate;

    private LocalDate expireDate;

    private Integer attachmentId;

    /* ---------- 查询条件 ---------- */

    private Integer page;

    private Integer size;

    private String status;

    private String warnStatus;
}
