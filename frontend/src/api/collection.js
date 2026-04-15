import request from '@/utils/request'

/** 收款计划列表 */
export function getReceiptPlanList(params) {
  return request.get('/api/v1/collection/plans', { params })
}

/** 新增收款计划 */
export function createReceiptPlan(data) {
  return request.post('/api/v1/collection/plans', data)
}

/** 更新收款计划 */
export function updateReceiptPlan(id, data) {
  return request.put(`/api/v1/collection/plans/${id}`, data)
}

/** 确认回款 */
export function confirmReceipt(id, actualAmount, actualDate) {
  return request.patch(`/api/v1/collection/plans/${id}/confirm`, null, { params: { actualAmount, actualDate } })
}

/** 删除收款计划 */
export function deleteReceiptPlan(id) {
  return request.delete(`/api/v1/collection/plans/${id}`)
}

/** 逾期数量 */
export function getOverdueCount() {
  return request.get('/api/v1/collection/overdue-count')
}

/** 跟进记录列表 */
export function getFollowUpList(planId) {
  return request.get(`/api/v1/collection/plans/${planId}/follow-ups`)
}

/** 新增跟进记录 */
export function createFollowUp(data) {
  return request.post('/api/v1/collection/follow-ups', data)
}
