import request from '@/utils/request'

export function getSupplierList(params) {
  return request.get('/api/v1/suppliers', { params })
}

export function getAllSuppliers() {
  return request.get('/api/v1/suppliers/all')
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
