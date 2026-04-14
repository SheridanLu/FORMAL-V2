import { describe, it, expect } from 'vitest'
import { maskPhone, maskIdCard, maskBankCard, maskEmail } from '../desensitize'

describe('desensitize', () => {
  describe('maskPhone', () => {
    it('应正确脱敏手机号', () => {
      expect(maskPhone('13812345678')).toBe('138****5678')
    })
    it('空值返回原值', () => {
      expect(maskPhone('')).toBe('')
      expect(maskPhone(null)).toBe(null)
    })
  })

  describe('maskIdCard', () => {
    it('应正确脱敏18位身份证', () => {
      expect(maskIdCard('110101199001011234')).toBe('110***********1234')
    })
  })

  describe('maskBankCard', () => {
    it('应正确脱敏银行卡号', () => {
      expect(maskBankCard('6222021234561234')).toBe('****1234')
    })
  })

  describe('maskEmail', () => {
    it('应正确脱敏邮箱', () => {
      expect(maskEmail('sheridan@example.com')).toBe('s***n@example.com')
    })
  })
})
