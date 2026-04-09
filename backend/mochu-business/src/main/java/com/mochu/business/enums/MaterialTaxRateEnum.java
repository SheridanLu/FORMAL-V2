package com.mochu.business.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 材料税率枚举 — 六档固定，禁止自定义
 */
public enum MaterialTaxRateEnum {

    RATE_0(0),
    RATE_1(1),
    RATE_3(3),
    RATE_6(6),
    RATE_9(9),
    RATE_13(13);

    private final int rate;

    MaterialTaxRateEnum(int rate) {
        this.rate = rate;
    }

    public int getRate() { return rate; }

    public static boolean isValid(Integer rate) {
        if (rate == null) return false;
        return Arrays.stream(values()).anyMatch(e -> e.rate == rate);
    }

    public static List<Integer> toList() {
        return Arrays.stream(values())
                .map(MaterialTaxRateEnum::getRate)
                .collect(Collectors.toList());
    }
}
