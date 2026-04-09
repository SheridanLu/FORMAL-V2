package com.mochu.business.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 材料分类枚举 — 固定三类，禁止新增删减修改
 */
public enum MaterialCategoryEnum {

    EQUIPMENT("设备"),
    MATERIAL("材料"),
    LABOR("人工");

    private final String label;

    MaterialCategoryEnum(String label) {
        this.label = label;
    }

    public String getLabel() { return label; }

    public static boolean isValid(String label) {
        return Arrays.stream(values()).anyMatch(e -> e.label.equals(label));
    }

    public static List<Map<String, String>> toList() {
        return Arrays.stream(values())
                .map(e -> Map.of("code", e.label, "label", e.label))
                .collect(Collectors.toList());
    }
}
