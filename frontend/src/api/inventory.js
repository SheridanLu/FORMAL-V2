import request from '@/utils/request'

export function getInboundList(params) {
  return request.get('/api/v1/inventory/inbound', { params })
}

export function createInbound(data) {
  return request.post('/api/v1/inventory/inbound', data)
}

export function updateInbound(id, data) {
  return request.put(`/api/v1/inventory/inbound/${id}`, data)
}

export function deleteInbound(id) {
  return request.delete(`/api/v1/inventory/inbound/${id}`)
}

export function getOutboundList(params) {
  return request.get('/api/v1/inventory/outbound', { params })
}

export function createOutbound(data) {
  return request.post('/api/v1/inventory/outbound', data)
}

export function updateOutbound(id, data) {
  return request.put(`/api/v1/inventory/outbound/${id}`, data)
}

export function deleteOutbound(id) {
  return request.delete(`/api/v1/inventory/outbound/${id}`)
}

export function getReturnList(params) {
  return request.get('/api/v1/inventory/return', { params })
}

export function createReturn(data) {
  return request.post('/api/v1/inventory/return', data)
}

export function updateReturn(id, data) {
  return request.put(`/api/v1/inventory/return/${id}`, data)
}

export function deleteReturn(id) {
  return request.delete(`/api/v1/inventory/return/${id}`)
}

export function getStockList(params) {
  return request.get('/api/v1/inventory/stock', { params })
}

// 提交入库/出库/退库
export function submitInbound(id) {
  return request.patch(`/api/v1/inventory/inbound/${id}/status`, { status: 'submitted' })
}

export function submitOutbound(id) {
  return request.patch(`/api/v1/inventory/outbound/${id}/status`, { status: 'submitted' })
}

export function submitReturn(id) {
  return request.patch(`/api/v1/inventory/return/${id}/status`, { status: 'submitted' })
}

// V3.2 盘点管理
export function getCheckList(params) {
  return request.get('/api/v1/inventory/check', { params })
}

export function createCheck(data) {
  return request.post('/api/v1/inventory/check', data)
}

export function updateCheck(id, data) {
  return request.put(`/api/v1/inventory/check/${id}`, data)
}

export function deleteCheck(id) {
  return request.delete(`/api/v1/inventory/check/${id}`)
}

export function submitCheck(id) {
  return request.patch(`/api/v1/inventory/check/${id}/status`, { status: 'submitted' })
}

export function getCheckById(id) {
  return request.get(`/api/v1/inventory/check/${id}`)
}

// ==================== 库存预警 ====================

export function getInventoryAlerts(params) {
  return request({ url: '/api/v1/inventory/alerts', method: 'get', params })
}

export function getTriggeredAlerts(params) {
  return request({ url: '/api/v1/inventory/alerts/triggered', method: 'get', params })
}

export function saveInventoryAlert(data) {
  return request({ url: '/api/v1/inventory/alerts', method: 'post', data })
}

export function deleteInventoryAlert(id) {
  return request({ url: `/api/v1/inventory/alerts/${id}`, method: 'delete' })
}

// ==================== 库存调拨 ====================

export function getInventoryTransfers(params) {
  return request({ url: '/api/v1/inventory/transfers', method: 'get', params })
}

export function createInventoryTransfer(data) {
  return request({ url: '/api/v1/inventory/transfers', method: 'post', data })
}

export function confirmInventoryTransfer(id) {
  return request({ url: `/api/v1/inventory/transfers/${id}/confirm`, method: 'put' })
}

export function cancelInventoryTransfer(id) {
  return request({ url: `/api/v1/inventory/transfers/${id}/cancel`, method: 'put' })
}
