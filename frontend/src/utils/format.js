import dayjs from 'dayjs'

/**
 * 格式化金额 — DECIMAL(14,2)
 * @param {number|string} value
 * @returns {string} 如 "1,234,567.89"
 */
export function formatMoney(value) {
  const num = parseFloat(value)
  if (isNaN(num)) return '0.00'
  const abs = Math.abs(num)
  const str = abs.toFixed(2)
  const [intPart, decPart] = str.split('.')
  const withCommas = intPart.replace(/\B(?=(\d{3})+(?!\d))/g, ',')
  return (num < 0 ? '-' : '') + withCommas + '.' + decPart
}

/**
 * 格式化日期
 * @param {string|Date} value
 * @param {string} fmt
 */
export function formatDate(value, fmt = 'YYYY-MM-DD') {
  if (!value) return '-'
  return dayjs(value).format(fmt)
}

/**
 * 格式化日期时间
 */
export function formatDateTime(value) {
  return formatDate(value, 'YYYY-MM-DD HH:mm:ss')
}

/**
 * 格式化文件大小
 * @param {number} bytes
 */
export function formatFileSize(bytes) {
  if (!bytes || bytes === 0) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(1024))
  return (bytes / Math.pow(1024, i)).toFixed(2) + ' ' + units[i]
}

/**
 * 格式化数量 — DECIMAL(14,4)
 */
export function formatQuantity(value) {
  const num = parseFloat(value)
  if (isNaN(num)) return '0'
  // 去除末尾多余的0
  return parseFloat(num.toFixed(4)).toString()
}
