import { ref } from 'vue'
import {
  PAYMENT_TYPES, INVOICE_TYPES, REIMBURSE_TYPES, OUTBOUND_TYPES,
  CERT_TYPES, EDUCATION_LEVELS, HR_CONTRACT_TYPES, RESIGN_TYPES,
  EXCEPTION_BIZ_TYPES, CASE_TYPES
} from '@/utils/dict'

/**
 * 字典/枚举 composable — 提供常用枚举的缓存访问
 */

// 状态枚举
const STATUS_OPTIONS = [
  { value: 'draft',      label: '草稿' },
  { value: 'pending',    label: '待审批' },
  { value: 'approved',   label: '已审批' },
  { value: 'rejected',   label: '已驳回' },
  { value: 'cancelled',  label: '已取消' },
  { value: 'confirmed',  label: '已确认' },
  { value: 'collected',  label: '已领取' },
  { value: 'returned',   label: '已退回' },
  { value: 'closed',     label: '已关闭' },
  { value: 'active',     label: '进行中' },
  { value: 'suspended',  label: '已暂停' },
  { value: 'terminated', label: '已终止' },
  { value: 'completed',  label: '已完成' },
  { value: 'virtual',    label: '虚拟' },
  { value: 'entity',     label: '实体' },
  { value: 'overdue',    label: '逾期' },
  { value: 'normal',     label: '正常' },
  { value: 'paid',       label: '已付款' },
  { value: 'unpaid',     label: '未付款' }
]

// 合同类型
const CONTRACT_TYPE_OPTIONS = [
  { value: 'income',          label: '收入合同' },
  { value: 'expense',         label: '支出合同' },
  { value: 'labor',           label: '劳务合同' },
  { value: 'material',        label: '材料合同' },
  { value: 'equipment_lease', label: '设备租赁合同' },
  { value: 'professional',    label: '专业分包合同' },
  { value: 'other',           label: '其他合同' }
]

// 物料分类
const MATERIAL_CATEGORY_OPTIONS = [
  { value: 'steel',     label: '钢材' },
  { value: 'cement',    label: '水泥' },
  { value: 'sand',      label: '砂石' },
  { value: 'concrete',  label: '混凝土' },
  { value: 'wood',      label: '木材' },
  { value: 'electric',  label: '电气材料' },
  { value: 'plumbing',  label: '水暖材料' },
  { value: 'decor',     label: '装饰材料' },
  { value: 'other',     label: '其他' }
]

// 税率
const TAX_RATE_OPTIONS = [
  { value: '0.00',  label: '0%' },
  { value: '1.00',  label: '1%' },
  { value: '3.00',  label: '3%' },
  { value: '6.00',  label: '6%' },
  { value: '9.00',  label: '9%' },
  { value: '13.00', label: '13%' }
]

// 变更类型
const CHANGE_TYPE_OPTIONS = [
  { value: 'design',       label: '设计变更' },
  { value: 'engineering',  label: '工程变更' },
  { value: 'visa',         label: '签证变更' },
  { value: 'other',        label: '其他变更' }
]

// 将 dict.js 中的 {key: label} 对象转为 [{value, label}] 数组
function toOptions(map) {
  return Object.entries(map).map(([value, label]) => ({ value, label }))
}

// 付款类型
const PAYMENT_TYPE_OPTIONS = toOptions(PAYMENT_TYPES)

// 发票类型
const INVOICE_TYPE_OPTIONS = toOptions(INVOICE_TYPES)

// 报销类型
const REIMBURSE_TYPE_OPTIONS = toOptions(REIMBURSE_TYPES)

// 出库类型
const OUTBOUND_TYPE_OPTIONS = toOptions(OUTBOUND_TYPES)

// 证书类型
const CERT_TYPE_OPTIONS = toOptions(CERT_TYPES)

// 学历
const EDUCATION_LEVEL_OPTIONS = toOptions(EDUCATION_LEVELS)

// 劳动合同类型
const HR_CONTRACT_TYPE_OPTIONS = toOptions(HR_CONTRACT_TYPES)

// 离职类型
const RESIGN_TYPE_OPTIONS = toOptions(RESIGN_TYPES)

// 异常工单业务类型
const EXCEPTION_BIZ_TYPE_OPTIONS = toOptions(EXCEPTION_BIZ_TYPES)

// 案例类型
const CASE_TYPE_OPTIONS = toOptions(CASE_TYPES)

const dictCache = {}

export function useDict(dictType) {
  const options = ref(getDictOptions(dictType))
  return { options, getLabel: (val) => getLabelByValue(dictType, val) }
}

function getDictOptions(type) {
  const map = {
    status: STATUS_OPTIONS,
    contract_type: CONTRACT_TYPE_OPTIONS,
    material_category: MATERIAL_CATEGORY_OPTIONS,
    tax_rate: TAX_RATE_OPTIONS,
    change_type: CHANGE_TYPE_OPTIONS,
    payment_type: PAYMENT_TYPE_OPTIONS,
    invoice_type: INVOICE_TYPE_OPTIONS,
    reimburse_type: REIMBURSE_TYPE_OPTIONS,
    outbound_type: OUTBOUND_TYPE_OPTIONS,
    cert_type: CERT_TYPE_OPTIONS,
    education_level: EDUCATION_LEVEL_OPTIONS,
    hr_contract_type: HR_CONTRACT_TYPE_OPTIONS,
    resign_type: RESIGN_TYPE_OPTIONS,
    exception_biz_type: EXCEPTION_BIZ_TYPE_OPTIONS,
    case_type: CASE_TYPE_OPTIONS
  }
  return map[type] || []
}

function getLabelByValue(type, value) {
  const opts = getDictOptions(type)
  return opts.find(o => o.value === value)?.label || value
}
