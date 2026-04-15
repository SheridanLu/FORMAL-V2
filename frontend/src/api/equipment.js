import request from '@/utils/request'

export function getEquipmentList(params) {
  return request.get('/api/v1/equipments', { params })
}

export function getEquipment(id) {
  return request.get(`/api/v1/equipments/${id}`)
}

export function createEquipment(data) {
  return request.post('/api/v1/equipments', data)
}

export function updateEquipment(id, data) {
  return request.put(`/api/v1/equipments/${id}`, data)
}

export function assignEquipment(id, projectId, location) {
  return request.patch(`/api/v1/equipments/${id}/assign`, null, { params: { projectId, location } })
}

export function updateEquipmentStatus(id, status) {
  return request.patch(`/api/v1/equipments/${id}/status`, null, { params: { status } })
}

export function deleteEquipment(id) {
  return request.delete(`/api/v1/equipments/${id}`)
}
