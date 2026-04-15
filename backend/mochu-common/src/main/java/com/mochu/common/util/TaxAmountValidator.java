package com.mochu.common.util;

import com.mochu.common.exception.BusinessException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

/**
 * 含税/不含税/税率 三值一致性校验器
 *
 * <p>V3.0 §3.4: 后端校验三值联动的一致性，误差不超过 0.01。
 * 税率仅允许固定枚举值：0, 1, 3, 6, 9, 13（百分比）。
 */
public final class TaxAmountValidator {

    private TaxAmountValidator() {}

    /** 合法税率集合（百分比） */
    private static final Set<Integer> VALID_TAX_RATES = Set.of(0, 1, 3, 6, 9, 13);

    /** 允许的最大误差 */
    private static final BigDecimal MAX_TOLERANCE = new BigDecimal("0.01");

    /**
     * 校验税率是否为合法枚举值
     *
     * @param taxRate 税率（百分比，如 13 表示 13%）
     * @throws BusinessException 非法税率
     */
    public static void validateTaxRate(BigDecimal taxRate) {
        if (taxRate == null) {
            return; // 允许为空（某些场景不需要税率）
        }
        int rateInt;
        try {
            rateInt = taxRate.intValueExact();
        } catch (ArithmeticException e) {
            throw new BusinessException("税率必须为整数，允许值：0, 1, 3, 6, 9, 13");
        }
        if (!VALID_TAX_RATES.contains(rateInt)) {
            throw new BusinessException("无效的税率 " + rateInt + "%，允许值：0, 1, 3, 6, 9, 13");
        }
    }

    /**
     * 校验含税/不含税/税率三值一致性
     *
     * <p>公式：含税金额 = 不含税金额 × (1 + 税率/100)
     * <p>误差允许范围：±0.01
     *
     * @param amountWithTax    含税金额
     * @param amountWithoutTax 不含税金额
     * @param taxRate          税率（百分比）
     * @throws BusinessException 不一致时抛出
     */
    public static void validateConsistency(BigDecimal amountWithTax,
                                           BigDecimal amountWithoutTax,
                                           BigDecimal taxRate) {
        // 三值都为空时跳过
        if (amountWithTax == null && amountWithoutTax == null) {
            return;
        }
        // 三值都有才校验
        if (amountWithTax == null || amountWithoutTax == null || taxRate == null) {
            return;
        }

        validateTaxRate(taxRate);

        BigDecimal rateDecimal = taxRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal expectedWithTax = amountWithoutTax.multiply(BigDecimal.ONE.add(rateDecimal))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal diff = amountWithTax.subtract(expectedWithTax).abs();
        if (diff.compareTo(MAX_TOLERANCE) > 0) {
            throw new BusinessException(
                    String.format("含税金额(%s)与不含税金额(%s)在税率%s%%下不一致，" +
                                    "预期含税金额=%s，偏差=%s（允许误差0.01）",
                            amountWithTax, amountWithoutTax, taxRate.intValue(),
                            expectedWithTax, diff));
        }
    }

    /**
     * 完整校验（税率枚举 + 三值一致性），供 Service 层直接调用
     */
    public static void validate(BigDecimal amountWithTax,
                                BigDecimal amountWithoutTax,
                                BigDecimal taxRate) {
        validateTaxRate(taxRate);
        validateConsistency(amountWithTax, amountWithoutTax, taxRate);
    }
}
