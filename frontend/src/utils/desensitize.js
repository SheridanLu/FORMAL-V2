/**
 * 手机号脱敏: 138****1234
 */
export function maskPhone(val) {
  if (!val || val.length < 7) return val
  return val.substring(0, 3) + '****' + val.substring(val.length - 4)
}

/**
 * 身份证脱敏: 110***********1234
 */
export function maskIdCard(val) {
  if (!val || val.length < 8) return val
  return val.substring(0, 3) + '*'.repeat(val.length - 7) + val.substring(val.length - 4)
}

/**
 * 银行卡脱敏: ****1234
 */
export function maskBankCard(val) {
  if (!val || val.length < 4) return val
  return '****' + val.substring(val.length - 4)
}

/**
 * 邮箱脱敏: s***n@example.com
 */
export function maskEmail(val) {
  if (!val || !val.includes('@')) return val
  const [name, domain] = val.split('@')
  if (name.length <= 2) return name[0] + '***@' + domain
  return name[0] + '***' + name[name.length - 1] + '@' + domain
}

/**
 * 通用脱敏
 */
export function mask(val, type) {
  const handlers = { phone: maskPhone, idcard: maskIdCard, bank: maskBankCard, email: maskEmail }
  const handler = handlers[type]
  return handler ? handler(val) : val
}
