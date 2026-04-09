package com.mochu.business.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 计量单位枚举 — 八项固定，禁止自由填写
 */
public enum MaterialUnitEnum {

    METER("米"),
    TON("吨"),
    ROLL("卷"),
    BOX("箱"),
    SET_MACHINE("台"),
    ITEM("项"),
    PIECE("只"),
    SET("套");

    private final String label;

    MaterialUnitEnum(String label) {
        this.label = label;
    }

    public String getLabel() { return label; }

    public static boolean isValid(String label) {
        return Arrays.stream(values()).anyMatch(e -> e.label.equals(label));
    }

    public static List<String> toList() {
        return Arrays.stream(values())
                .map(MaterialUnitEnum::getLabel)
                .collect(Collectors.toList());
    }
}
