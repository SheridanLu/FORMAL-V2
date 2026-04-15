import { watch, ref } from 'vue'

/**
 * 含税/不含税/税率 三值联动计算 Hook
 *
 * V3.0 §3.4: 税率固定枚举（0/1/3/6/9/13%），含税与不含税金额自动联动。
 *
 * 联动规则:
 * 1. 未选税率时 → 禁止联动计算，仅允许手动输入
 * 2. 修改含税金额 → 自动计算不含税 = 含税 / (1 + 税率/100)
 * 3. 修改不含税金额 → 自动计算含税 = 不含税 * (1 + 税率/100)
 * 4. 修改税率 → 根据最后编辑的金额字段重新计算另一个
 * 5. 所有金额保留2位小数（四舍五入）
 * 6. 内置防循环触发机制
 *
 * @param {Reactive} form - 包含 amountWithTax / amountWithoutTax / taxRate 的 reactive 对象
 * @param {Object} options - 可选配置
 * @param {string} options.withTaxKey - 含税金额字段名，默认 'amountWithTax'
 * @param {string} options.withoutTaxKey - 不含税金额字段名，默认 'amountWithoutTax'
 * @param {string} options.taxRateKey - 税率字段名，默认 'taxRate'
 *
 * @returns {{ TAX_RATES, lastEdited }}
 */
export function useTaxAmountLinkage(form, options = {}) {
  const {
    withTaxKey = 'amountWithTax',
    withoutTaxKey = 'amountWithoutTax',
    taxRateKey = 'taxRate'
  } = options

  /** 合法税率枚举 */
  const TAX_RATES = [0, 1, 3, 6, 9, 13]

  /** 记录最后编辑的金额字段，用于税率变更时决定重算方向 */
  const lastEdited = ref('withTax')

  /** 防循环标志 */
  let computing = false

  /**
   * 四舍五入到2位小数
   */
  function round2(val) {
    return Math.round(val * 100) / 100
  }

  /**
   * 校验税率是否有效
   */
  function isValidRate(rate) {
    return rate != null && TAX_RATES.includes(Number(rate))
  }

  // 监听含税金额变化
  watch(
    () => form[withTaxKey],
    (newVal) => {
      if (computing) return
      if (newVal == null || newVal === '') return
      lastEdited.value = 'withTax'

      const rate = form[taxRateKey]
      if (!isValidRate(rate)) return

      computing = true
      const withTax = Number(newVal)
      const rateDecimal = Number(rate) / 100
      form[withoutTaxKey] = round2(withTax / (1 + rateDecimal))
      computing = false
    }
  )

  // 监听不含税金额变化
  watch(
    () => form[withoutTaxKey],
    (newVal) => {
      if (computing) return
      if (newVal == null || newVal === '') return
      lastEdited.value = 'withoutTax'

      const rate = form[taxRateKey]
      if (!isValidRate(rate)) return

      computing = true
      const withoutTax = Number(newVal)
      const rateDecimal = Number(rate) / 100
      form[withTaxKey] = round2(withoutTax * (1 + rateDecimal))
      computing = false
    }
  )

  // 监听税率变化 → 根据最后编辑的金额字段重算另一个
  watch(
    () => form[taxRateKey],
    (newRate) => {
      if (computing) return
      if (!isValidRate(newRate)) return

      const rateDecimal = Number(newRate) / 100
      computing = true

      if (lastEdited.value === 'withTax') {
        const withTax = form[withTaxKey]
        if (withTax != null && withTax !== '') {
          form[withoutTaxKey] = round2(Number(withTax) / (1 + rateDecimal))
        }
      } else {
        const withoutTax = form[withoutTaxKey]
        if (withoutTax != null && withoutTax !== '') {
          form[withTaxKey] = round2(Number(withoutTax) * (1 + rateDecimal))
        }
      }

      computing = false
    }
  )

  return {
    TAX_RATES,
    lastEdited
  }
}
