import request from '@/utils/request'

/** 暗项列表 */
export function getHiddenItemList(params) {
  return request.get('/api/v1/hidden-items', { params })
}

/** 暗项详情 */
export function getHiddenItemById(id) {
  return request.get(`/api/v1/hidden-items/${id}`)
}

/** 新增暗项 */
export function createHiddenItem(data) {
  return request.post('/api/v1/hidden-items', data)
}

/** 更新暗项 */
export function updateHiddenItem(id, data) {
  return request.put(`/api/v1/hidden-items/${id}`, data)
}

/** 暗项状态更新 */
export function updateHiddenItemStatus(id, status) {
  return request.patch(`/api/v1/hidden-items/${id}/status`, { status })
}

/** 删除暗项 */
export function deleteHiddenItem(id) {
  return request.delete(`/api/v1/hidden-items/${id}`)
}

/** 暗项汇总 */
export function getHiddenItemSummary(projectId) {
  return request.get('/api/v1/hidden-items/summary', { params: { projectId } })
}
