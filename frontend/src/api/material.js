import request from '@/utils/request'

export function getMaterialList(params) {
  return request.get('/api/v1/materials', { params })
}

export function getAllMaterials() {
  return request.get('/api/v1/materials/all')
}

export function getMaterialById(id) {
  return request.get(`/api/v1/materials/${id}`)
}

export function batchCreateMaterial(data) {
  return request.post('/api/v1/materials/batch', data)
}

export function updateMaterial(id, data) {
  return request.put(`/api/v1/materials/${id}`, data)
}

export function deleteMaterial(id) {
  return request.delete(`/api/v1/materials/${id}`)
}
