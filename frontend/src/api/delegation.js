import request from '@/utils/request'

export function getDelegationList(params) {
  return request.get('/api/v1/admin/delegations', { params })
}

export function createDelegation(data) {
  return request.post('/api/v1/admin/delegations', data)
}

export function revokeDelegation(id) {
  return request.patch(`/api/v1/admin/delegations/${id}/revoke`)
}

export function deleteDelegation(id) {
  return request.delete(`/api/v1/admin/delegations/${id}`)
}
