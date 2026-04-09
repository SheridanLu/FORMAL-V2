package com.mochu.business.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 模板字段批量更新 DTO
 */
@Data
public class TplFieldUpdateDTO {

    @NotNull(message = "字段列表不能为空")
    private List<FieldItem> fields;

    @Data
    public static class FieldItem {
        private Integer id;
        private String fieldKey;
        private String fieldName;
        private String fieldType;
        private Integer required;
        private String optionsJson;
        private String defaultValue;
        private Integer sortOrder;
        private String placeholder;
        private Integer maxLength;
        private String validationRule;
    }
}
