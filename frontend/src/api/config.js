import request from '@/utils/request'

export function getConfigList(params) {
  return request.get('/api/v1/admin/configs', { params })
}

export function getConfigById(id) {
  return request.get(`/api/v1/admin/configs/${id}`)
}

export function createConfig(data) {
  return request.post('/api/v1/admin/configs', data)
}

export function updateConfig(id, data) {
  return request.put(`/api/v1/admin/configs/${id}`, data)
}

export function deleteConfig(id) {
  return request.delete(`/api/v1/admin/configs/${id}`)
}
