import request from '@/utils/request'

export function getPurchaseList(params) {
  return request.get('/api/v1/purchases', { params })
}

export function createPurchase(data) {
  return request.post('/api/v1/purchases', data)
}

export function updatePurchase(id, data) {
  return request.put(`/api/v1/purchases/${id}`, data)
}

export function updatePurchaseStatus(id, status) {
  return request.patch(`/api/v1/purchases/${id}/status`, { status })
}

export function getPurchaseItems(id) {
  return request.get(`/api/v1/purchases/${id}/items`)
}

export function deletePurchase(id) {
  return request.delete(`/api/v1/purchases/${id}`)
}

export function getSpotPurchaseList(params) {
  return request.get('/api/v1/spot-purchases', { params })
}

export function createSpotPurchase(data) {
  return request.post('/api/v1/spot-purchases', data)
}

export function updateSpotPurchase(id, data) {
  return request.put(`/api/v1/spot-purchases/${id}`, data)
}

export function deleteSpotPurchase(id) {
  return request.delete(`/api/v1/spot-purchases/${id}`)
}
