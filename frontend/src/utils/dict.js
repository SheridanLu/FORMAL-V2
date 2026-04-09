/**
 * 字典/枚举映射 — V3.2 19个业务状态 + 颜色
 */

export const STATUS_MAP = {
  draft:      { label: '草稿',   color: '#909399', type: 'info' },
  pending:    { label: '待审批', color: '#e6a23c', type: 'warning' },
  approved:   { label: '已审批', color: '#67c23a', type: 'success' },
  rejected:   { label: '已驳回', color: '#f56c6c', type: 'danger' },
  cancelled:  { label: '已取消', color: '#909399', type: 'info' },
  confirmed:  { label: '已确认', color: '#67c23a', type: 'success' },
  collected:  { label: '已领取', color: '#67c23a', type: 'success' },
  returned:   { label: '已退回', color: '#e6a23c', type: 'warning' },
  closed:     { label: '已关闭', color: '#909399', type: 'info' },
  active:     { label: '进行中', color: '#409eff', type: '' },
  suspended:  { label: '已暂停', color: '#e6a23c', type: 'warning' },
  terminated: { label: '已终止', color: '#f56c6c', type: 'danger' },
  completed:  { label: '已完成', color: '#67c23a', type: 'success' },
  virtual:    { label: '虚拟',   color: '#909399', type: 'info' },
  entity:     { label: '实体',   color: '#409eff', type: '' },
  overdue:    { label: '逾期',   color: '#f56c6c', type: 'danger' },
  normal:     { label: '正常',   color: '#67c23a', type: 'success' },
  paid:       { label: '已付款', color: '#67c23a', type: 'success' },
  unpaid:     { label: '未付款', color: '#e6a23c', type: 'warning' }
}

/**
 * 获取状态标签
 */
export function getStatusLabel(status) {
  return STATUS_MAP[status]?.label || status
}

/**
 * 获取状态颜色
 */
export function getStatusColor(status) {
  return STATUS_MAP[status]?.color || '#909399'
}

/**
 * 获取状态 Tag 类型
 */
export function getStatusType(status) {
  return STATUS_MAP[status]?.type ?? 'info'
}

/**
 * 合同类型枚举
 */
export const CONTRACT_TYPES = {
  income:          '收入合同',
  expense:         '支出合同',
  labor:           '劳务合同',
  material:        '材料合同',
  equipment_lease: '设备租赁合同',
  professional:    '专业分包合同',
  other:           '其他合同'
}

/**
 * 变更类型枚举
 */
export const CHANGE_TYPES = {
  design:      '设计变更',
  engineering: '工程变更',
  visa:        '签证变更',
  other:       '其他变更'
}

/**
 * 物料分类枚举
 */
export const MATERIAL_CATEGORIES = {
  steel:    '钢材',
  cement:   '水泥',
  sand:     '砂石',
  concrete: '混凝土',
  wood:     '木材',
  electric: '电气材料',
  plumbing: '水暖材料',
  decor:    '装饰材料',
  other:    '其他'
}

/**
 * 付款类型
 */
export const PAYMENT_TYPES = {
  advance:   '预付款',
  progress:  '进度款',
  final:     '结算款',
  retention: '质保金',
  other:     '其他'
}

/**
 * 发票类型
 */
export const INVOICE_TYPES = {
  special:  '增值税专用发票',
  ordinary: '增值税普通发票',
  receipt:  '收据'
}

/**
 * 报销类型
 */
export const REIMBURSE_TYPES = {
  travel:   '差旅费',
  office:   '办公费',
  material: '材料费',
  other:    '其他'
}

/**
 * 出库类型
 */
export const OUTBOUND_TYPES = {
  construction: '施工领用',
  transfer:     '调拨出库',
  scrap:        '报废出库',
  other:        '其他'
}

/**
 * 证书类型
 */
export const CERT_TYPES = {
  safety:        '安全证书',
  qualification: '资质证书',
  skill:         '技能证书',
  special:       '特种作业证'
}

/**
 * 学历
 */
export const EDUCATION_LEVELS = {
  high_school: '高中/中专',
  associate:   '大专',
  bachelor:    '本科',
  master:      '硕士',
  doctor:      '博士'
}

/**
 * 劳动合同类型
 */
export const HR_CONTRACT_TYPES = {
  fixed:   '固定期限',
  unfixed: '无固定期限',
  task:    '以完成工作任务为期限'
}

/**
 * 离职类型
 */
export const RESIGN_TYPES = {
  voluntary:   '主动离职',
  involuntary: '辞退',
  retirement:  '退休',
  expiry:      '合同到期'
}

/**
 * 异常工单业务类型
 */
export const EXCEPTION_BIZ_TYPES = {
  quality:  '质量问题',
  safety:   '安全问题',
  progress: '进度问题',
  cost:     '成本问题'
}

/**
 * 案例类型
 */
export const CASE_TYPES = {
  quality: '质量案例',
  safety:  '安全案例',
  dispute: '纠纷案例',
  other:   '其他案例'
}
