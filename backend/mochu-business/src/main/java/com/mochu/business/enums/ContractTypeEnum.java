package com.mochu.business.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 合同类型枚举 — 固定七类，禁止新增删减
 */
public enum ContractTypeEnum {

    EQUIPMENT_PURCHASE("equipment_purchase", "设备类采购合同"),
    MATERIAL_PURCHASE("material_purchase", "材料类采购合同"),
    SUBCONTRACT("subcontract", "专业分包采购合同"),
    LABOR("labor", "劳务施工合同"),
    TECH_SERVICE("tech_service", "技术服务合同"),
    SOFTWARE("software", "软件工程合同"),
    OTHER("other", "其他类合同");

    private final String code;
    private final String label;

    ContractTypeEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }

    public static boolean isValid(String code) {
        return Arrays.stream(values()).anyMatch(e -> e.code.equals(code));
    }

    public static List<Map<String, String>> toList() {
        return Arrays.stream(values())
                .map(e -> Map.of("code", e.code, "label", e.label))
                .collect(Collectors.toList());
    }
}
