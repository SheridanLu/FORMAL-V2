import request from '@/utils/request'

/**
 * 用户管理 API — 对照 V3.2 §5.9.1
 */

export function getUserList(params) {
  return request.get('/api/v1/admin/users', { params })
}

export function getUserDetail(id) {
  return request.get(`/api/v1/admin/users/${id}`)
}

export function createUser(data) {
  return request.post('/api/v1/admin/users', data)
}

export function updateUser(id, data) {
  return request.put(`/api/v1/admin/users/${id}`, data)
}

export function deleteUser(id) {
  return request.delete(`/api/v1/admin/users/${id}`)
}

export function updateUserStatus(id, status) {
  return request.patch(`/api/v1/admin/users/${id}/status`, { status })
}

export function assignUserRoles(id, roleIds) {
  return request.put(`/api/v1/admin/users/${id}/roles`, { role_ids: roleIds })
}

export function resetUserPassword(id, newPassword) {
  return request.post(`/api/v1/admin/users/${id}/reset-password`, { new_password: newPassword })
}
