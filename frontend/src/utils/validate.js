/**
 * 表单校验规则工厂
 */

/**
 * 手机号校验
 */
export const phoneRule = {
  pattern: /^1[3-9]\d{9}$/,
  message: '请输入正确的手机号',
  trigger: 'blur'
}

/**
 * 身份证号校验
 */
export const idCardRule = {
  pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/,
  message: '请输入正确的身份证号',
  trigger: 'blur'
}

/**
 * 金额校验 — DECIMAL(14,2)
 */
export function moneyRule(message = '请输入正确的金额') {
  return {
    validator: (rule, value, callback) => {
      if (value === '' || value === undefined || value === null) {
        callback()
        return
      }
      const num = parseFloat(value)
      if (isNaN(num) || num < 0) {
        callback(new Error(message))
      } else if (!/^\d{1,12}(\.\d{1,2})?$/.test(String(value))) {
        callback(new Error('整数最多12位，小数最多2位'))
      } else {
        callback()
      }
    },
    trigger: 'blur'
  }
}

/**
 * 数量校验 — DECIMAL(14,4)
 */
export function quantityRule(message = '请输入正确的数量') {
  return {
    validator: (rule, value, callback) => {
      if (value === '' || value === undefined || value === null) {
        callback()
        return
      }
      const num = parseFloat(value)
      if (isNaN(num) || num < 0) {
        callback(new Error(message))
      } else if (!/^\d{1,10}(\.\d{1,4})?$/.test(String(value))) {
        callback(new Error('整数最多10位，小数最多4位'))
      } else {
        callback()
      }
    },
    trigger: 'blur'
  }
}

/**
 * 邮箱校验
 */
export const emailRule = {
  type: 'email',
  message: '请输入正确的邮箱',
  trigger: 'blur'
}

/**
 * 必填规则
 */
export function requiredRule(message = '必填') {
  return { required: true, message, trigger: 'blur' }
}
