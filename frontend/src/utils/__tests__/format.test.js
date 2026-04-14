import { describe, it, expect } from 'vitest'
import { formatMoney, formatDate } from '../format'

describe('format', () => {
  describe('formatMoney', () => {
    it('应格式化金额为千分位', () => {
      expect(formatMoney(1234567.89)).toBe('1,234,567.89')
    })
    it('空值返回 0.00', () => {
      expect(formatMoney(null)).toBe('0.00')
    })
  })
})
