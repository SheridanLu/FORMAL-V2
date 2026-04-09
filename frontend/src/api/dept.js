import request from '@/utils/request'

/**
 * 部门管理 API — 对照 V3.2 §5.9.3
 */

export function getDeptTree() {
  return request.get('/api/v1/admin/depts/tree')
}

export function getDeptDetail(id) {
  return request.get(`/api/v1/admin/depts/${id}`)
}

export function createDept(data) {
  return request.post('/api/v1/admin/depts', data)
}

export function updateDept(id, data) {
  return request.put(`/api/v1/admin/depts/${id}`, data)
}

export function updateDeptStatus(id, status) {
  return request.patch(`/api/v1/admin/depts/${id}/status`, { status })
}

export function deleteDept(id) {
  return request.delete(`/api/v1/admin/depts/${id}`)
}
