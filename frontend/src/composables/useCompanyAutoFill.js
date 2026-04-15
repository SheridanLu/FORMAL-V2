import { ref } from 'vue'
import { searchCompanyInfo, getCompanyInfoByName } from '@/api/companyInfo'

/**
 * 公司信息自动填充 Hook
 *
 * V3.0 §3.5: 在合同/供应商表单中输入甲方/乙方名称时，
 * 自动搜索已有公司并回填联系人、电话、银行等信息。
 *
 * @param {Reactive} form - 包含需要回填字段的 reactive 对象
 * @param {Object} options - 字段映射配置
 * @param {string} options.nameKey - 公司名称字段名 (default: 'partyA')
 * @param {Object} options.fieldMap - 公司属性 → 表单字段的映射
 *
 * @returns {{ suggestions, loading, onSearch, onSelect, onBlur }}
 *
 * @example
 * // 合同表单甲方自动填充
 * const { suggestions, loading, onSearch, onSelect } = useCompanyAutoFill(form, {
 *   nameKey: 'partyA',
 *   fieldMap: {
 *     credit_code: 'partyCreditCode',
 *     contact_phone: 'partyAPhone',
 *     bank_name: 'partyABank',
 *     bank_account: 'partyAAccount',
 *   }
 * })
 */
export function useCompanyAutoFill(form, options = {}) {
  const {
    nameKey = 'partyA',
    fieldMap = {}
  } = options

  const suggestions = ref([])
  const loading = ref(false)

  /** 搜索防抖计时器 */
  let debounceTimer = null

  /**
   * 模糊搜索公司（防抖300ms）
   * @param {string} keyword - 搜索关键词
   */
  function onSearch(keyword) {
    clearTimeout(debounceTimer)
    if (!keyword || keyword.length < 2) {
      suggestions.value = []
      return
    }
    debounceTimer = setTimeout(async () => {
      loading.value = true
      try {
        const res = await searchCompanyInfo(keyword)
        suggestions.value = res.data || []
      } catch {
        suggestions.value = []
      } finally {
        loading.value = false
      }
    }, 300)
  }

  /**
   * 选中一个公司后自动回填
   * @param {Object} company - 公司信息对象 (snake_case)
   */
  function onSelect(company) {
    if (!company) return

    // 回填公司名称
    form[nameKey] = company.company_name || company.companyName || ''

    // 按映射回填其他字段
    for (const [companyField, formField] of Object.entries(fieldMap)) {
      if (company[companyField] !== undefined) {
        form[formField] = company[companyField]
      }
    }
  }

  /**
   * 输入框失焦时尝试精确匹配并回填
   */
  async function onBlur() {
    const name = form[nameKey]
    if (!name || name.length < 2) return

    try {
      const res = await getCompanyInfoByName(name)
      if (res.data) {
        onSelect(res.data)
      }
    } catch {
      // 查不到不处理
    }
  }

  /**
   * el-autocomplete 的 fetch-suggestions 回调适配
   * @param {string} queryString
   * @param {Function} callback
   */
  function fetchSuggestions(queryString, callback) {
    if (!queryString || queryString.length < 2) {
      callback([])
      return
    }
    loading.value = true
    searchCompanyInfo(queryString)
      .then(res => {
        callback(res.data || [])
      })
      .catch(() => {
        callback([])
      })
      .finally(() => {
        loading.value = false
      })
  }

  return {
    suggestions,
    loading,
    onSearch,
    onSelect,
    onBlur,
    fetchSuggestions
  }
}
