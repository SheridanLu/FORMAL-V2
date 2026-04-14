import request from '@/utils/request'

export function getSupplierList(params) {
  return request.get('/api/v1/suppliers', { params })
}

export function getAllSuppliers() {
  return request.get('/api/v1/suppliers/all')
}

// ==================== 供应商评价 ====================

export function getSupplierRatingList(params) {
  return request({ url: '/api/v1/suppliers/rating', method: 'get', params })
}

export function getSupplierRatingSummary() {
  return request({ url: '/api/v1/suppliers/rating/summary', method: 'get' })
}

export function createSupplierRating(data) {
  return request({ url: '/api/v1/suppliers/rating', method: 'post', data })
}

export function deleteSupplierRating(id) {
  return request({ url: `/api/v1/suppliers/rating/${id}`, method: 'delete' })
}

export function getSupplierById(id) {
  return request.get(`/api/v1/suppliers/${id}`)
}

export function createSupplier(data) {
  return request.post('/api/v1/suppliers', data)
}

export function updateSupplier(id, data) {
  return request.put(`/api/v1/suppliers/${id}`, data)
}

export function deleteSupplier(id) {
  return request.delete(`/api/v1/suppliers/${id}`)
}
